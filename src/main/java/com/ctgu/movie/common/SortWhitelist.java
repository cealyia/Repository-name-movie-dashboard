package com.ctgu.movie.common;

/**
 * 排序字段白名单。
 * MyBatis 的 ${} 是直接拼字符串，如果让前端传列名会有 SQL 注入风险，
 * 所以只允许映射表里列出的几个字段，其它一律回退到默认排序。
 */
public final class SortWhitelist {

    private SortWhitelist() {
    }

    /**
     * @param sortBy   前端传来的排序字段：ratingCount / avgScore / releaseYear / title
     * @param sortOrder 前端传来的方向：asc / desc
     * @return 可以安全拼进 ORDER BY 的片段
     */
    public static String resolve(String sortBy, String sortOrder) {
        String column = switch (sortBy == null ? "" : sortBy) {
            case "avgScore" -> "avg_score";
            case "releaseYear" -> "release_year";
            case "title" -> "title";
            default -> "rating_count";
        };
        String direction = "asc".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC";
        // 追加 movie_id 作为次级排序，保证翻页时顺序稳定，不会出现同一条数据重复出现
        return column + " " + direction + ", movie_id ASC";
    }
}
