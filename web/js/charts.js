/**
 * 图表渲染模块。
 * 每个 renderXxx(chart, data) 只负责把数据转成 ECharts 的 option，
 * 不关心数据从哪来，方便单独调试。
 */
const DashboardCharts = (function () {

    const PALETTE = ['#6c8cff', '#29d3c2', '#ffb020', '#ff5c7a', '#a78bfa',
        '#38bdf8', '#f472b6', '#84cc16', '#fb923c', '#22d3ee'];

    const TEXT = '#8b97b0';
    const LINE = '#232c42';
    const SPLIT = '#1a2233';

    const AXIS = {
        axisLine: { lineStyle: { color: LINE } },
        axisTick: { show: false },
        axisLabel: { color: TEXT, fontSize: 11 },
        splitLine: { lineStyle: { color: SPLIT } }
    };

    const TOOLTIP = {
        backgroundColor: 'rgba(16, 22, 36, .95)',
        borderColor: LINE,
        textStyle: { color: '#e8ecf5', fontSize: 12 },
        extraCssText: 'box-shadow: 0 8px 24px rgba(0,0,0,.45); border-radius: 8px;'
    };

    const EMPTY_OPTION = {
        title: {
            text: '暂无数据',
            left: 'center',
            top: 'middle',
            textStyle: { color: '#4d5972', fontSize: 13, fontWeight: 'normal' }
        }
    };

    function empty(chart) {
        chart.clear();
        chart.setOption(EMPTY_OPTION);
    }

    /** 顶部 KPI 数字 */
    function renderKpi(kpi) {
        const map = {
            movieCount: formatInt(kpi.movieCount),
            ratedMovieCount: formatInt(kpi.ratedMovieCount),
            ratingCount: formatInt(kpi.ratingCount),
            avgScore: kpi.avgScore === null ? '—' : Number(kpi.avgScore).toFixed(2),
            userCount: formatInt(kpi.userCount),
            maxRatingCount: formatInt(kpi.maxRatingCount),
            highScoreRatio: kpi.highScoreRatio === null ? '—' : Number(kpi.highScoreRatio).toFixed(1)
        };
        Object.entries(map).forEach(([key, value]) => {
            const el = document.querySelector('[data-kpi="' + key + '"]');
            if (el) {
                el.textContent = value;
            }
        });

        const changeEl = document.querySelector('[data-kpi="change"]');
        if (!changeEl) {
            return;
        }
        if (kpi.ratingCountChange === null || kpi.ratingCountChange === undefined) {
            changeEl.textContent = '环比 —（无上一周期数据）';
            changeEl.className = 'kpi-note';
            return;
        }
        const up = kpi.ratingCountChange >= 0;
        changeEl.textContent = '环比 ' + (up ? '+' : '') + kpi.ratingCountChange.toFixed(1) + '%（上期 '
            + formatInt(kpi.prevRatingCount) + ' 条）';
        changeEl.className = 'kpi-note ' + (up ? 'up' : 'down');
    }

    /** 评分趋势：柱状（评分数）+ 折线（平均分） */
    function renderTrend(chart, points, granularity) {
        if (!points || points.length === 0) {
            return empty(chart);
        }
        const periods = points.map(p => p.period);
        const counts = points.map(p => p.ratingCount);
        const scores = points.map(p => p.avgScore);

        chart.setOption({
            color: [PALETTE[0], PALETTE[1]],
            tooltip: Object.assign({
                trigger: 'axis',
                axisPointer: { type: 'shadow' },
                formatter: function (items) {
                    const title = items[0].axisValue;
                    const lines = items.map(it => {
                        const unit = it.seriesName === '平均分' ? ' 分' : ' 条';
                        return it.marker + it.seriesName + '：' + it.value + unit;
                    });
                    return title + '<br/>' + lines.join('<br/>');
                }
            }, TOOLTIP),
            legend: {
                data: ['评分条数', '平均分'],
                top: 0,
                right: 8,
                itemWidth: 12,
                itemHeight: 8,
                textStyle: { color: TEXT, fontSize: 11 }
            },
            grid: { left: 58, right: 58, top: 44, bottom: 36 },
            xAxis: {
                type: 'category',
                data: periods,
                axisLine: AXIS.axisLine,
                axisTick: AXIS.axisTick,
                axisLabel: {
                    color: TEXT,
                    fontSize: 10,
                    hideOverlap: true,
                    formatter: granularity === 'day' ? (v => v.slice(5)) : (v => v)
                }
            },
            yAxis: [
                {
                    type: 'value',
                    name: '条',
                    nameTextStyle: { color: TEXT, fontSize: 10 },
                    axisLabel: AXIS.axisLabel,
                    splitLine: AXIS.splitLine
                },
                {
                    type: 'value',
                    name: '分',
                    min: 0,
                    max: 10,
                    nameTextStyle: { color: TEXT, fontSize: 10 },
                    axisLabel: AXIS.axisLabel,
                    splitLine: { show: false }
                }
            ],
            dataZoom: points.length > 40 ? [{ type: 'inside' }] : [],
            series: [
                {
                    name: '评分条数',
                    type: 'bar',
                    data: counts,
                    barMaxWidth: 22,
                    itemStyle: {
                        borderRadius: [3, 3, 0, 0],
                        color: {
                            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
                            colorStops: [
                                { offset: 0, color: 'rgba(108,140,255,.95)' },
                                { offset: 1, color: 'rgba(108,140,255,.15)' }
                            ]
                        }
                    }
                },
                {
                    name: '平均分',
                    type: 'line',
                    yAxisIndex: 1,
                    data: scores,
                    smooth: true,
                    symbol: 'circle',
                    symbolSize: 4,
                    lineStyle: { width: 2 },
                    itemStyle: { color: PALETTE[1] }
                }
            ]
        }, true);
    }

    /** 类型对比：柱状（影片数）+ 折线（平均分） */
    function renderGenres(chart, stats) {
        if (!stats || stats.length === 0) {
            return empty(chart);
        }
        const top = stats.slice(0, 12);
        chart.setOption({
            color: [PALETTE[0], PALETTE[2]],
            tooltip: Object.assign({
                trigger: 'axis',
                axisPointer: { type: 'shadow' }
            }, TOOLTIP),
            legend: {
                data: ['影片数', '平均分'],
                top: 0,
                right: 8,
                itemWidth: 12,
                itemHeight: 8,
                textStyle: { color: TEXT, fontSize: 11 }
            },
            grid: { left: 48, right: 46, top: 42, bottom: top.length > 8 ? 72 : 40 },
            xAxis: {
                type: 'category',
                data: top.map(s => s.genre),
                axisLine: AXIS.axisLine,
                axisTick: AXIS.axisTick,
                axisLabel: { color: TEXT, fontSize: 10, interval: 0, rotate: top.length > 8 ? 32 : 0 }
            },
            yAxis: [
                {
                    type: 'value',
                    name: '部',
                    nameTextStyle: { color: TEXT, fontSize: 10 },
                    axisLabel: AXIS.axisLabel,
                    splitLine: AXIS.splitLine
                },
                {
                    type: 'value',
                    name: '分',
                    min: 0,
                    max: 10,
                    nameTextStyle: { color: TEXT, fontSize: 10 },
                    axisLabel: AXIS.axisLabel,
                    splitLine: { show: false }
                }
            ],
            series: [
                {
                    name: '影片数',
                    type: 'bar',
                    data: top.map(s => s.movieCount),
                    barMaxWidth: 26,
                    itemStyle: { borderRadius: [4, 4, 0, 0], color: PALETTE[0] }
                },
                {
                    name: '平均分',
                    type: 'line',
                    yAxisIndex: 1,
                    data: top.map(s => s.avgScore),
                    smooth: true,
                    symbolSize: 5,
                    lineStyle: { width: 2 }
                }
            ]
        }, true);
    }

    /** 评分区间分布：环形图 */
    function renderRatingPie(chart, buckets) {
        if (!buckets || buckets.length === 0) {
            return empty(chart);
        }
        const total = buckets.reduce((sum, b) => sum + b.movieCount, 0);
        const colors = ['#29d3c2', '#6c8cff', '#a78bfa', '#ffb020', '#ff5c7a'];
        chart.setOption({
            color: colors,
            tooltip: Object.assign({
                trigger: 'item',
                formatter: '{b}<br/>影片数：{c} 部（{d}%）'
            }, TOOLTIP),
            legend: {
                orient: 'vertical',
                right: 4,
                top: 'center',
                itemWidth: 10,
                itemHeight: 10,
                textStyle: { color: TEXT, fontSize: 11 }
            },
            graphic: [{
                type: 'text',
                left: '30%',
                top: 'middle',
                style: {
                    text: total + '\n部影片',
                    fill: '#e8ecf5',
                    fontSize: 14,
                    fontWeight: 'bold',
                    textAlign: 'center',
                    lineHeight: 20
                }
            }],
            series: [{
                type: 'pie',
                radius: ['48%', '70%'],
                center: ['34%', '50%'],
                avoidLabelOverlap: true,
                label: { show: false },
                labelLine: { show: false },
                itemStyle: { borderColor: '#141a28', borderWidth: 2 },
                data: buckets.map((b, i) => ({
                    name: b.bucket + ' 分',
                    value: b.movieCount,
                    itemStyle: { color: colors[i % colors.length] }
                }))
            }]
        }, true);
    }

    /** TOP10：横向条形图 */
    function renderTop(chart, rows, metric) {
        if (!rows || rows.length === 0) {
            return empty(chart);
        }
        const ordered = rows.slice().reverse();
        const metricName = metric === 'avgScore' ? '平均分' : '评分数';
        chart.setOption({
            tooltip: Object.assign({
                trigger: 'axis',
                axisPointer: { type: 'shadow' },
                formatter: function (items) {
                    const item = items[0];
                    return item.name + '<br/>' + metricName + '：' + item.value
                        + (metric === 'avgScore' ? ' 分' : ' 条');
                }
            }, TOOLTIP),
            grid: { left: 8, right: 46, top: 12, bottom: 8, containLabel: true },
            xAxis: {
                type: 'value',
                axisLabel: AXIS.axisLabel,
                splitLine: AXIS.splitLine
            },
            yAxis: {
                type: 'category',
                data: ordered.map(r => shorten(r.title, 18)),
                axisLine: AXIS.axisLine,
                axisTick: { show: false },
                axisLabel: { color: '#cdd6e8', fontSize: 11 }
            },
            series: [{
                type: 'bar',
                data: ordered.map(r => metric === 'avgScore' ? r.avgScore : r.ratingCount),
                barMaxWidth: 16,
                itemStyle: {
                    borderRadius: [0, 4, 4, 0],
                    color: {
                        type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
                        colorStops: [
                            { offset: 0, color: 'rgba(41,211,194,.35)' },
                            { offset: 1, color: 'rgba(108,140,255,.95)' }
                        ]
                    }
                },
                label: {
                    show: true,
                    position: 'right',
                    color: TEXT,
                    fontSize: 11
                }
            }]
        }, true);
    }

    /** 标签词云 */
    function renderTags(chart, tags) {
        if (!tags || tags.length === 0) {
            return empty(chart);
        }
        const max = Math.max.apply(null, tags.map(t => t.value));
        const min = Math.min.apply(null, tags.map(t => t.value));
        // 注意：不把 color 写成函数放在 series.textStyle 上，
        // echarts-wordcloud 的 visualStyleMapper 只会读取具体颜色值，函数会导致文字不渲染。
        chart.setOption({
            tooltip: Object.assign({
                formatter: function (item) {
                    return item.name + '：' + item.value + ' 次';
                }
            }, TOOLTIP),
            series: [{
                type: 'wordCloud',
                shape: 'circle',
                left: 'center',
                top: 'center',
                width: '96%',
                height: '92%',
                sizeRange: [11, 32],
                rotationRange: [-45, 45],
                rotationStep: 45,
                gridSize: 6,
                drawOutOfBound: false,
                textStyle: {
                    fontWeight: 'bold'
                },
                emphasis: {
                    textStyle: { textShadowBlur: 12, textShadowColor: 'rgba(108,140,255,.8)' }
                },
                data: tags.map(function (t, index) {
                    const fontSize = 11 + (max === min ? 0 : (t.value - min) / (max - min)) * 22;
                    return {
                        name: shorten(t.name, 16),
                        value: t.value,
                        textStyle: {
                            fontSize: fontSize,
                            color: PALETTE[index % PALETTE.length]
                        }
                    };
                })
            }]
        }, true);
    }

    /** 类型多维对比雷达图：4 个维度都换算成 0-100 的相对值 */
    function renderRadar(chart, stats) {
        if (!stats || stats.length === 0) {
            return empty(chart);
        }
        const top = stats.slice(0, 5).filter(s => s.movieCount > 0);
        if (top.length === 0) {
            return empty(chart);
        }
        const maxMovies = Math.max.apply(null, top.map(s => s.movieCount)) || 1;
        const maxRatings = Math.max.apply(null, top.map(s => s.ratingCount)) || 1;
        const maxAvgPerMovie = Math.max.apply(null, top.map(s => s.ratingCount / Math.max(s.movieCount, 1))) || 1;

        chart.setOption({
            color: PALETTE,
            tooltip: Object.assign({ trigger: 'item' }, TOOLTIP),
            legend: {
                bottom: 0,
                itemWidth: 10,
                itemHeight: 8,
                textStyle: { color: TEXT, fontSize: 11 }
            },
            radar: {
                center: ['50%', '46%'],
                radius: '68%',
                indicator: [
                    { name: '影片数', max: 100 },
                    { name: '评分数', max: 100 },
                    { name: '平均分', max: 100 },
                    { name: '单片平均评分数', max: 100 }
                ],
                axisName: { color: TEXT, fontSize: 11 },
                splitLine: { lineStyle: { color: SPLIT } },
                splitArea: { areaStyle: { color: ['rgba(20,26,40,.4)', 'rgba(16,20,31,.4)'] } },
                axisLine: { lineStyle: { color: SPLIT } }
            },
            series: [{
                type: 'radar',
                areaStyle: { opacity: .06 },
                lineStyle: { width: 2 },
                symbolSize: 3,
                data: top.map(s => ({
                    name: s.genre,
                    value: [
                        round1(s.movieCount / maxMovies * 100),
                        round1(s.ratingCount / maxRatings * 100),
                        round1((s.avgScore || 0) / 10 * 100),
                        round1((s.ratingCount / Math.max(s.movieCount, 1)) / maxAvgPerMovie * 100)
                    ]
                }))
            }]
        }, true);
    }

    /** 年代 × 类型热力图：横轴类型、纵轴年代，格子里的数字是影片数 */
    function renderHeatmap(chart, cells) {
        if (!cells || cells.length === 0) {
            return empty(chart);
        }
        const decades = Array.from(new Set(cells.map(c => c.decade))).sort((a, b) => a - b);
        const genres = Array.from(new Set(cells.map(c => c.genre)));
        const data = cells.map(c => [genres.indexOf(c.genre), decades.indexOf(c.decade), c.movieCount]);
        const maxValue = Math.max.apply(null, cells.map(c => c.movieCount));

        chart.setOption({
            tooltip: Object.assign({
                formatter: function (p) {
                    return genres[p.value[0]] + ' · ' + decades[p.value[1]] + ' 年代<br/>影片数：' + p.value[2] + ' 部';
                }
            }, TOOLTIP),
            grid: { left: 8, right: 16, top: 12, bottom: 74, containLabel: true },
            xAxis: {
                type: 'category',
                data: genres,
                splitArea: { show: true, areaStyle: { color: ['rgba(255,255,255,.02)', 'rgba(255,255,255,0)'] } },
                axisLine: AXIS.axisLine,
                axisTick: { show: false },
                axisLabel: { color: TEXT, fontSize: 11, interval: 0, rotate: 32 }
            },
            yAxis: {
                type: 'category',
                data: decades.map(d => d + 's'),
                splitArea: { show: true, areaStyle: { color: ['rgba(255,255,255,.02)', 'rgba(255,255,255,0)'] } },
                axisLine: AXIS.axisLine,
                axisTick: { show: false },
                axisLabel: { color: TEXT, fontSize: 11 }
            },
            visualMap: {
                min: 0,
                max: maxValue,
                calculable: true,
                orient: 'horizontal',
                left: 'center',
                bottom: 6,
                itemWidth: 12,
                itemHeight: 90,
                textStyle: { color: TEXT, fontSize: 11 },
                inRange: { color: ['#101828', '#1e3a8a', '#4b6bff', '#29d3c2', '#ffe08a'] }
            },
            series: [{
                type: 'heatmap',
                data: data,
                label: { show: true, color: '#dfe6f5', fontSize: 10 },
                emphasis: {
                    itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,.5)' }
                }
            }]
        }, true);
    }

    /** 详情抽屉里的单部影片月度趋势 */
    function renderDetail(chart, monthly) {
        if (!monthly || monthly.length === 0) {
            return empty(chart);
        }
        chart.setOption({
            tooltip: Object.assign({ trigger: 'axis' }, TOOLTIP),
            grid: { left: 40, right: 40, top: 26, bottom: 26 },
            xAxis: {
                type: 'category',
                data: monthly.map(m => m.period),
                axisLine: AXIS.axisLine,
                axisTick: { show: false },
                axisLabel: { color: TEXT, fontSize: 10, hideOverlap: true }
            },
            yAxis: [
                { type: 'value', axisLabel: AXIS.axisLabel, splitLine: AXIS.splitLine },
                { type: 'value', min: 0, max: 10, axisLabel: AXIS.axisLabel, splitLine: { show: false } }
            ],
            series: [
                {
                    name: '评分数',
                    type: 'bar',
                    data: monthly.map(m => m.ratingCount),
                    barMaxWidth: 14,
                    itemStyle: { color: 'rgba(108,140,255,.75)', borderRadius: [3, 3, 0, 0] }
                },
                {
                    name: '平均分',
                    type: 'line',
                    yAxisIndex: 1,
                    data: monthly.map(m => m.avgScore),
                    smooth: true,
                    symbolSize: 4,
                    lineStyle: { width: 2, color: PALETTE[1] },
                    itemStyle: { color: PALETTE[1] }
                }
            ]
        }, true);
    }

    function formatInt(value) {
        if (value === null || value === undefined) {
            return '—';
        }
        return Number(value).toLocaleString('zh-CN');
    }

    function round1(v) {
        return Math.round(v * 10) / 10;
    }

    function shorten(text, max) {
        if (!text) {
            return '';
        }
        return text.length > max ? text.slice(0, max) + '…' : text;
    }

    return {
        renderKpi,
        renderTrend,
        renderGenres,
        renderRatingPie,
        renderTop,
        renderTags,
        renderRadar,
        renderHeatmap,
        renderDetail,
        palette: PALETTE
    };
})();
