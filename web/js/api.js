/**
 * 接口封装：所有请求都从这里发出。
 * 页面里不直接写 fetch，统一处理了：查询参数拼接、Result 结构解包、错误提示。
 */
const API = (function () {
    const BASE = '';

    /**
     * @param {string} path 接口路径，例如 /api/stats/overview
     * @param {object} params 查询参数对象，数组会自动展开成重复参数
     */
    async function request(path, params) {
        const search = new URLSearchParams();
        Object.entries(params || {}).forEach(([key, value]) => {
            if (value === undefined || value === null || value === '') {
                return;
            }
            if (Array.isArray(value)) {
                value.forEach(item => search.append(key, item));
            } else {
                search.append(key, value);
            }
        });
        const query = search.toString();
        const url = BASE + path + (query ? '?' + query : '');

        let response;
        try {
            response = await fetch(url, { headers: { 'Accept': 'application/json' } });
        } catch (e) {
            throw new Error('无法连接后端服务，请确认 Spring Boot 已启动（http://localhost:8080）');
        }

        let body;
        try {
            body = await response.json();
        } catch (e) {
            throw new Error('接口返回的不是 JSON（HTTP ' + response.status + '），请检查后端日志');
        }
        if (!response.ok || body.code !== 200) {
            throw new Error(body.message || ('请求失败（HTTP ' + response.status + '）'));
        }
        return body.data;
    }

    return {
        meta: () => request('/api/meta/filters'),
        overview: q => request('/api/stats/overview', q),
        trend: (q, granularity) => request('/api/stats/trend', Object.assign({}, q, { granularity })),
        genres: q => request('/api/stats/genres', q),
        ratingDistribution: q => request('/api/stats/rating-distribution', q),
        top: (q, metric) => request('/api/stats/top', Object.assign({}, q, { metric, limit: 10 })),
        tags: (q, limit) => request('/api/stats/tags', Object.assign({}, q, { limit })),
        heatmap: q => request('/api/stats/heatmap', q),
        movies: q => request('/api/movies', q),
        movie: id => request('/api/movies/' + id)
    };
})();
