/**
 * 页面主逻辑：维护筛选状态、调用接口、把数据交给图表模块渲染。
 * 数据流：筛选条件 -> buildQuery() -> API -> renderXxx()
 */
(function () {

    const state = {
        meta: null,
        filters: {
            genres: [],
            yearFrom: null,
            yearTo: null,
            ratingFrom: null,
            ratingTo: null,
            dateFrom: null,
            dateTo: null,
            keyword: ''
        },
        page: 1,
        size: 10,
        sortBy: 'ratingCount',
        sortOrder: 'desc',
        trendGranularity: 'month',
        topMetric: 'ratingCount',
        lastTopRows: []
    };

    const charts = {};
    let detailChart = null;
    let toastTimer = null;

    // ---------- 小工具 ----------
    const $ = id => document.getElementById(id);

    function showToast(message) {
        const toast = $('toast');
        toast.textContent = message;
        toast.hidden = false;
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () {
            toast.hidden = true;
        }, 3600);
    }

    function debounce(fn, delay) {
        let timer = null;
        return function () {
            const args = arguments;
            clearTimeout(timer);
            timer = setTimeout(() => fn.apply(null, args), delay);
        };
    }

    /** 请求期间给图表容器加半透明效果，让用户知道数据在刷新 */
    async function withLoading(element, task) {
        element.classList.add('is-loading');
        try {
            await task();
        } finally {
            element.classList.remove('is-loading');
        }
    }

    function toDateString(date) {
        const y = date.getFullYear();
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const d = String(date.getDate()).padStart(2, '0');
        return y + '-' + m + '-' + d;
    }

    function parseDate(text) {
        return new Date(text + 'T00:00:00');
    }

    function escapeHtml(text) {
        return String(text == null ? '' : text)
            .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    }

    // ---------- 组装查询参数 ----------
    function buildQuery() {
        const f = state.filters;
        return {
            genres: f.genres,
            yearFrom: f.yearFrom,
            yearTo: f.yearTo,
            ratingFrom: f.ratingFrom,
            ratingTo: f.ratingTo,
            dateFrom: f.dateFrom,
            dateTo: f.dateTo,
            keyword: f.keyword
        };
    }

    function buildPageQuery() {
        return Object.assign(buildQuery(), {
            page: state.page,
            size: state.size,
            sortBy: state.sortBy,
            sortOrder: state.sortOrder
        });
    }

    // ---------- 初始化 ----------
    async function init() {
        initCharts();
        bindEvents();
        try {
            state.meta = await API.meta();
        } catch (e) {
            showToast(e.message);
            return;
        }
        renderMeta();
        applyDefaultFilters();
        await refreshAll();
    }

    function initCharts() {
        charts.trend = echarts.init($('trendChart'));
        charts.genre = echarts.init($('genreChart'));
        charts.rating = echarts.init($('ratingChart'));
        charts.top = echarts.init($('topChart'));
        charts.tag = echarts.init($('tagChart'));
        charts.radar = echarts.init($('radarChart'));
        charts.heatmap = echarts.init($('heatmapChart'));

        window.addEventListener('resize', debounce(function () {
            Object.values(charts).forEach(c => c.resize());
            if (detailChart) {
                detailChart.resize();
            }
        }, 120));
    }

    function renderMeta() {
        const meta = state.meta;
        $('metaSummary').textContent = '共 ' + meta.totalRatings.toLocaleString('zh-CN') + ' 条评分 · '
            + meta.totalMovies.toLocaleString('zh-CN') + ' 部影片 · '
            + meta.totalUsers + ' 位用户';
        $('metaDateRange').textContent = meta.dateMin + ' ~ ' + meta.dateMax;
        $('metaYearRange').textContent = meta.yearMin + ' ~ ' + meta.yearMax;

        const box = $('genreChips');
        box.innerHTML = '';
        meta.genres.forEach(function (g) {
            const chip = document.createElement('span');
            chip.className = 'chip';
            chip.dataset.genre = g.genreName;
            chip.textContent = g.genreName;
            box.appendChild(chip);
        });

        const fromSel = $('yearFrom');
        const toSel = $('yearTo');
        fromSel.innerHTML = '';
        toSel.innerHTML = '';
        for (let y = meta.yearMin; y <= meta.yearMax; y++) {
            fromSel.appendChild(new Option(y, y));
            toSel.appendChild(new Option(y, y));
        }

        $('dateFrom').min = meta.dateMin;
        $('dateFrom').max = meta.dateMax;
        $('dateTo').min = meta.dateMin;
        $('dateTo').max = meta.dateMax;
    }

    function applyDefaultFilters() {
        const meta = state.meta;
        const maxDate = parseDate(meta.dateMax);
        const defaultFrom = new Date(maxDate.getFullYear() - 3, 0, 1);
        const from = defaultFrom < parseDate(meta.dateMin) ? parseDate(meta.dateMin) : defaultFrom;

        state.filters.yearFrom = meta.yearMin;
        state.filters.yearTo = meta.yearMax;
        state.filters.dateFrom = toDateString(from);
        state.filters.dateTo = meta.dateMax;

        $('yearFrom').value = meta.yearMin;
        $('yearTo').value = meta.yearMax;
        $('dateFrom').value = state.filters.dateFrom;
        $('dateTo').value = state.filters.dateTo;
    }

    // ---------- 刷新 ----------
    async function refreshAll() {
        const query = buildQuery();
        await Promise.all([
            loadKpi(query),
            loadTrend(query),
            loadGenres(query),
            loadRatingDistribution(query),
            loadTop(query),
            loadTags(query),
            loadHeatmap(query),
            loadTable()
        ]);
    }

    async function loadKpi(query) {
        try {
            const data = await API.overview(query);
            DashboardCharts.renderKpi(data);
        } catch (e) {
            showToast(e.message);
        }
    }

    async function loadTrend(query) {
        await withLoading($('trendChart'), async function () {
            try {
                const data = await API.trend(query, state.trendGranularity);
                DashboardCharts.renderTrend(charts.trend, data, state.trendGranularity);
            } catch (e) {
                showToast(e.message);
            }
        });
    }

    /** 类型对比柱状图 + 雷达图共用同一份类型统计数据，只请求一次 */
    async function loadGenres(query) {
        await withLoading($('genreChart'), async function () {
            try {
                const data = await API.genres(query);
                DashboardCharts.renderGenres(charts.genre, data);
                DashboardCharts.renderRadar(charts.radar, data);
            } catch (e) {
                showToast(e.message);
            }
        });
    }

    async function loadRatingDistribution(query) {
        await withLoading($('ratingChart'), async function () {
            try {
                const data = await API.ratingDistribution(query);
                DashboardCharts.renderRatingPie(charts.rating, data);
            } catch (e) {
                showToast(e.message);
            }
        });
    }

    async function loadTop(query) {
        await withLoading($('topChart'), async function () {
            try {
                const data = await API.top(query, state.topMetric);
                state.lastTopRows = data;
                DashboardCharts.renderTop(charts.top, data, state.topMetric);
            } catch (e) {
                showToast(e.message);
            }
        });
    }

    async function loadTags(query) {
        await withLoading($('tagChart'), async function () {
            try {
                const data = await API.tags(query, 45);
                DashboardCharts.renderTags(charts.tag, data);
            } catch (e) {
                showToast(e.message);
            }
        });
    }

    async function loadHeatmap(query) {
        await withLoading($('heatmapChart'), async function () {
            try {
                const data = await API.heatmap(query);
                DashboardCharts.renderHeatmap(charts.heatmap, data);
            } catch (e) {
                showToast(e.message);
            }
        });
    }

    async function loadTable() {
        const tbody = $('tableBody');
        tbody.classList.add('is-loading');
        try {
            const page = await API.movies(buildPageQuery());
            renderTable(page);
        } catch (e) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty">加载失败：' + escapeHtml(e.message) + '</td></tr>';
            showToast(e.message);
        } finally {
            tbody.classList.remove('is-loading');
        }
    }

    function renderTable(page) {
        const tbody = $('tableBody');
        // 页码越界（例如筛选后总页数变少）：自动回到最后一页
        if ((!page.list || page.list.length === 0) && page.page > 1 && page.pages > 0) {
            state.page = page.pages;
            loadTable();
            return;
        }
        tbody.innerHTML = '';
        if (!page.list || page.list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty">没有符合条件的数据，试着放宽筛选条件</td></tr>';
        } else {
            page.list.forEach(function (row) {
                const tr = document.createElement('tr');
                const genreHtml = (row.genres || []).slice(0, 3)
                    .map(g => '<span class="tag-pill">' + escapeHtml(g) + '</span>').join('');
                tr.innerHTML = '<td class="title-cell" title="' + escapeHtml(row.title) + '">' + escapeHtml(row.title) + '</td>'
                    + '<td>' + genreHtml + '</td>'
                    + '<td>' + (row.releaseYear || '—') + '</td>'
                    + '<td class="num">' + (row.avgScore === null ? '—' : Number(row.avgScore).toFixed(2)) + '</td>'
                    + '<td class="num">' + Number(row.ratingCount).toLocaleString('zh-CN') + '</td>';
                tr.addEventListener('click', function () {
                    openDetail(row.movieId);
                });
                tbody.appendChild(tr);
            });
        }
        $('pageInfo').textContent = '第 ' + page.page + ' / ' + Math.max(page.pages, 1)
            + ' 页 · 共 ' + Number(page.total).toLocaleString('zh-CN') + ' 部';
        $('prevPage').disabled = page.page <= 1;
        $('nextPage').disabled = page.page >= page.pages;
        updateSortHeader();
    }

    function updateSortHeader() {
        document.querySelectorAll('.sortable').forEach(function (th) {
            th.classList.remove('sort-asc', 'sort-desc');
            if (th.dataset.sort === state.sortBy) {
                th.classList.add(state.sortOrder === 'asc' ? 'sort-asc' : 'sort-desc');
            }
        });
    }

    // ---------- 详情抽屉 ----------
    async function openDetail(movieId) {
        $('drawer').hidden = false;
        $('drawerMask').hidden = false;
        $('detailTitle').textContent = '加载中…';
        $('detailMeta').textContent = '';
        $('detailGenres').innerHTML = '';
        $('detailTags').innerHTML = '';

        try {
            const detail = await API.movie(movieId);
            $('detailTitle').textContent = detail.title;
            $('detailMeta').textContent = '上映年份：' + (detail.releaseYear || '未知')
                + ' · 平均分：' + (detail.avgScore === null ? '—' : Number(detail.avgScore).toFixed(2))
                + ' · 评分数：' + Number(detail.ratingCount).toLocaleString('zh-CN');
            $('detailGenres').innerHTML = (detail.genres || [])
                .map(g => '<span>' + escapeHtml(g) + '</span>').join('');
            $('detailTags').innerHTML = (detail.tags && detail.tags.length)
                ? detail.tags.map(t => '<span>' + escapeHtml(t) + '</span>').join('')
                : '<span style="background:none;color:#5d6b88">该影片暂无标签</span>';

            if (!detailChart) {
                detailChart = echarts.init($('detailChart'));
            }
            DashboardCharts.renderDetail(detailChart, detail.monthly);
            detailChart.resize();
        } catch (e) {
            $('detailTitle').textContent = '加载失败';
            $('detailMeta').textContent = e.message;
            showToast(e.message);
        }
    }

    function closeDetail() {
        $('drawer').hidden = true;
        $('drawerMask').hidden = true;
    }

    // ---------- 事件绑定 ----------
    function bindEvents() {
        const refresh = debounce(refreshAll, 200);

        $('genreChips').addEventListener('click', function (e) {
            const chip = e.target.closest('.chip');
            if (!chip) {
                return;
            }
            toggleGenre(chip.dataset.genre);
        });

        $('yearFrom').addEventListener('change', function () {
            state.filters.yearFrom = Number(this.value);
            state.page = 1;
            refresh();
        });
        $('yearTo').addEventListener('change', function () {
            state.filters.yearTo = Number(this.value);
            state.page = 1;
            refresh();
        });

        $('ratingRange').addEventListener('change', function () {
            applyRatingRange(this.value);
            state.page = 1;
            refreshAll();
        });

        $('dateFrom').addEventListener('change', function () {
            state.filters.dateFrom = this.value;
            state.page = 1;
            refresh();
        });
        $('dateTo').addEventListener('change', function () {
            state.filters.dateTo = this.value;
            state.page = 1;
            refresh();
        });

        $('keyword').addEventListener('input', debounce(function () {
            state.filters.keyword = this.value.trim();
            state.page = 1;
            refresh();
        }, 400));

        $('applyBtn').addEventListener('click', function () {
            state.page = 1;
            refreshAll();
        });

        $('resetBtn').addEventListener('click', function () {
            state.filters.genres = [];
            document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
            state.filters.ratingFrom = null;
            state.filters.ratingTo = null;
            $('ratingRange').value = '';
            $('keyword').value = '';
            state.filters.keyword = '';
            state.page = 1;
            state.sortBy = 'ratingCount';
            state.sortOrder = 'desc';
            applyDefaultFilters();
            refreshAll();
        });

        document.querySelectorAll('.btn-quick').forEach(function (btn) {
            btn.addEventListener('click', function () {
                const days = Number(this.dataset.days);
                const meta = state.meta;
                if (!days) {
                    state.filters.dateFrom = meta.dateMin;
                    state.filters.dateTo = meta.dateMax;
                } else {
                    const end = parseDate(meta.dateMax);
                    const start = new Date(end.getTime() - days * 24 * 3600 * 1000);
                    const min = parseDate(meta.dateMin);
                    state.filters.dateFrom = toDateString(start < min ? min : start);
                    state.filters.dateTo = meta.dateMax;
                }
                $('dateFrom').value = state.filters.dateFrom;
                $('dateTo').value = state.filters.dateTo;
                state.page = 1;
                refreshAll();
            });
        });

        $('trendGranularity').addEventListener('change', function () {
            state.trendGranularity = this.value;
            loadTrend(buildQuery());
        });

        $('topMetric').addEventListener('change', function () {
            state.topMetric = this.value;
            loadTop(buildQuery());
        });

        // 图表联动 1：点击类型柱体 -> 加/减该类型筛选
        charts.genre.on('click', function (params) {
            toggleGenre(params.name);
        });

        // 图表联动 2：点击评分扇区 -> 设置评分区间
        charts.rating.on('click', function (params) {
            const bucket = String(params.name).replace(' 分', '');
            const map = {
                '9.0-10': '9,10',
                '8.0-8.9': '8,9',
                '7.0-7.9': '7,8',
                '6.0-6.9': '6,7',
                '0-5.9': '0,6'
            };
            const value = map[bucket];
            if (value) {
                $('ratingRange').value = value;
                applyRatingRange(value);
                state.page = 1;
                refreshAll();
            }
        });

        // 图表联动 3：点击 TOP 柱体 -> 打开影片详情
        charts.top.on('click', function (params) {
            const rows = state.lastTopRows;
            if (rows && rows.length) {
                const target = rows[rows.length - 1 - params.dataIndex];
                if (target) {
                    openDetail(target.movieId);
                }
            }
        });

        document.querySelectorAll('.sortable').forEach(function (th) {
            th.addEventListener('click', function () {
                const field = this.dataset.sort;
                if (state.sortBy === field) {
                    state.sortOrder = state.sortOrder === 'asc' ? 'desc' : 'asc';
                } else {
                    state.sortBy = field;
                    state.sortOrder = 'desc';
                }
                state.page = 1;
                loadTable();
            });
        });

        $('prevPage').addEventListener('click', function () {
            if (state.page > 1) {
                state.page -= 1;
                loadTable();
            }
        });
        $('nextPage').addEventListener('click', function () {
            state.page += 1;
            loadTable();
        });

        $('drawerClose').addEventListener('click', closeDetail);
        $('drawerMask').addEventListener('click', closeDetail);
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                closeDetail();
            }
        });
    }

    /** 点击类型（chip 或柱状图）时切换该类型的选中状态 */
    function toggleGenre(genre) {
        const index = state.filters.genres.indexOf(genre);
        const chip = document.querySelector('.chip[data-genre="' + genre + '"]');
        if (index >= 0) {
            state.filters.genres.splice(index, 1);
            if (chip) {
                chip.classList.remove('active');
            }
        } else {
            state.filters.genres.push(genre);
            if (chip) {
                chip.classList.add('active');
            }
        }
        state.page = 1;
        refreshAll();
    }

    function applyRatingRange(value) {
        if (!value) {
            state.filters.ratingFrom = null;
            state.filters.ratingTo = null;
            return;
        }
        const parts = value.split(',');
        state.filters.ratingFrom = Number(parts[0]);
        state.filters.ratingTo = Number(parts[1]);
    }

    document.addEventListener('DOMContentLoaded', init);
})();
