package com.ctgu.movie.entity;

/**
 * 类型维度表 dim_genre 对应的实体。
 */
public class Genre {

    /** 类型 ID */
    private Integer genreId;

    /** 类型中文名 */
    private String genreName;

    /** 类型英文名（数据原始值） */
    private String genreNameEn;


    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getGenreNameEn() {
        return genreNameEn;
    }

    public void setGenreNameEn(String genreNameEn) {
        this.genreNameEn = genreNameEn;
    }
}
