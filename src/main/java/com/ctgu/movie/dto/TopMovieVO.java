package com.ctgu.movie.dto;

import java.util.List;

/**
 * TOP N 排行中的一部影片。
 */
public class TopMovieVO {

    /** 影片 ID */
    private Integer movieId;

    /** 片名 */
    private String title;

    /** 上映年份 */
    private Integer releaseYear;

    /** 评分条数 */
    private long ratingCount;

    /** 平均评分（10 分制） */
    private Double avgScore;

    /** 类型列表，由 Service 批量补全 */
    private List<String> genres;


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

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
