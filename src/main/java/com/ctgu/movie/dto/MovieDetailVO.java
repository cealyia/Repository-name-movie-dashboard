package com.ctgu.movie.dto;

import java.util.List;

/**
 * 影片详情：点击明细表格某一行或 TOP10 某根柱子时弹出的抽屉内容。
 */
public class MovieDetailVO {

    /** 影片 ID */
    private Integer movieId;

    /** 片名 */
    private String title;

    /** 上映年份 */
    private Integer releaseYear;

    /** 类型列表 */
    private List<String> genres;

    /** 用户打的标签（最多 20 个） */
    private List<String> tags;

    /** 评分条数 */
    private long ratingCount;

    /** 平均评分（10 分制） */
    private Double avgScore;

    /** 该影片按月聚合的评分趋势 */
    private List<TrendPointVO> monthly;

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public List<TrendPointVO> getMonthly() {
        return monthly;
    }

    public void setMonthly(List<TrendPointVO> monthly) {
        this.monthly = monthly;
    }
}
