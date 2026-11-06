package com.ctgu.movie.entity;

/**
 * 评分事实表 fact_rating 对应的实体。
 */
public class Rating {

    /** 评分记录 ID */
    private Long ratingId;

    /** 用户 ID */
    private Integer userId;

    /** 影片 ID */
    private Integer movieId;

    /** 评分（10 分制，原始为 5 星制 ×2） */
    private Double score;


    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
