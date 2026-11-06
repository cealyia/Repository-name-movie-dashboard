package com.ctgu.movie.entity;

/**
 * 影片维度表 dim_movie 对应的实体。
 */
public class Movie {

    /** 影片 ID（主键） */
    private Integer movieId;

    /** 片名，含上映年份，例如 Toy Story (1995) */
    private String title;

    /** 上映年份，从片名中解析 */
    private Integer releaseYear;


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
}
