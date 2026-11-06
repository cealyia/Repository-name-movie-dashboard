package com.ctgu.movie.dto;

/**
 * 热力图的一个格子：某个年代 × 某个类型的影片数。
 */
public class HeatCellVO {

    /** 年代，例如 1990 */
    private Integer decade;

    /** 类型名称 */
    private String genre;

    /** 该年代该类型的影片数（部） */
    private long movieCount;


    public Integer getDecade() {
        return decade;
    }

    public void setDecade(Integer decade) {
        this.decade = decade;
    }

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
}
