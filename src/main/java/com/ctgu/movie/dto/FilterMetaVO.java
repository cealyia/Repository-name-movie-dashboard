package com.ctgu.movie.dto;

import com.ctgu.movie.entity.Genre;

import java.time.LocalDate;
import java.util.List;

/**
 * 看板初始化元数据：类型下拉框、年份滑块、日期选择器的可选范围。
 * 前端一进来先请求它，后面所有筛选都基于这里的范围。
 */
public class FilterMetaVO {

    /** 全部类型 */
    private List<Genre> genres;

    /** 数据中最早的上映年份 */
    private Integer yearMin;

    /** 数据中最晚的上映年份 */
    private Integer yearMax;

    /** 最早一条评分日期 */
    private LocalDate dateMin;

    /** 最晚一条评分日期 */
    private LocalDate dateMax;

    /** 影片总数（部） */
    private long totalMovies;

    /** 评分总数（条） */
    private long totalRatings;

    /** 用户总数（人） */
    private long totalUsers;

    /** 标签总数（条） */
    private long totalTags;

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public Integer getYearMin() {
        return yearMin;
    }

    public void setYearMin(Integer yearMin) {
        this.yearMin = yearMin;
    }

    public Integer getYearMax() {
        return yearMax;
    }

    public void setYearMax(Integer yearMax) {
        this.yearMax = yearMax;
    }

    public LocalDate getDateMin() {
        return dateMin;
    }

    public void setDateMin(LocalDate dateMin) {
        this.dateMin = dateMin;
    }

    public LocalDate getDateMax() {
        return dateMax;
    }

    public void setDateMax(LocalDate dateMax) {
        this.dateMax = dateMax;
    }

    public long getTotalMovies() {
        return totalMovies;
    }

    public void setTotalMovies(long totalMovies) {
        this.totalMovies = totalMovies;
    }

    public long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalTags() {
        return totalTags;
    }

    public void setTotalTags(long totalTags) {
        this.totalTags = totalTags;
    }
}
