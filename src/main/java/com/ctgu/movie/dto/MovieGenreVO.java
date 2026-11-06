package com.ctgu.movie.dto;

/**
 * 影片与类型的关联行，用于批量补全每部影片的类型。
 */
public class MovieGenreVO {

    /** 影片 ID */
    private Integer movieId;

    /** 类型名称 */
    private String genreName;


    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
