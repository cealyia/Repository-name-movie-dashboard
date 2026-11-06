package com.ctgu.movie.dto;

/**
 * 类型维度统计：用于柱状图、雷达图。
 */
public class GenreStatVO {

    /** 类型名称，例如 剧情 */
    private String genre;

    /** 该类型下的影片数（部） */
    private long movieCount;

    /** 该类型下影片获得的评分条数 */
    private long ratingCount;

    /** 该类型下影片的平均评分（10 分制） */
    private Double avgScore;


    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getMovieCount() {
        return movieCount;
    }

    public void setMovieCount(long movieCount) {
        this.movieCount = movieCount;
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
}
