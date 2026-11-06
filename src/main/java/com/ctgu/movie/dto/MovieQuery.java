package com.ctgu.movie.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * 看板的统一筛选条件。
 * 所有图表和表格接口都接收同一套参数，保证「筛选一次，全屏联动」。
 */
public class MovieQuery {

    /** 类型（可多选），例如 ["剧情", "科幻"] */
    private List<String> genres;

    /** 上映年份下限，例如 1990 */
    private Integer yearFrom;

    /** 上映年份上限，例如 2018 */
    private Integer yearTo;

    /** 影片平均评分下限（10 分制） */
    private Double ratingFrom;

    /** 影片平均评分上限（10 分制） */
    private Double ratingTo;

    /** 评分时间范围：开始日期 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    /** 评分时间范围：结束日期 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    /** 片名关键字（模糊查询） */
    private String keyword;

    /** 页码，从 1 开始 */
    @Min(value = 1, message = "页码不能小于 1")
    private int page = 1;

    /** 每页条数 */
    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    private int size = 10;

    /** 排序字段：ratingCount / avgScore / releaseYear / title */
    private String sortBy = "ratingCount";

    /** 排序方向：asc / desc */
    private String sortOrder = "desc";

    /**
     * 把空值补成默认值，避免每个 SQL 都写一遍判空。
     * from/to 覆盖全部数据范围，前端不传也能查。
     */
    public void normalize() {
        if (dateFrom == null) {
            dateFrom = LocalDate.of(1900, 1, 1);
        }
        if (dateTo == null) {
            dateTo = LocalDate.of(2999, 12, 31);
        }
        if (dateFrom.isAfter(dateTo)) {
            LocalDate tmp = dateFrom;
            dateFrom = dateTo;
            dateTo = tmp;
        }
        if (yearFrom != null && yearTo != null && yearFrom > yearTo) {
            Integer tmp = yearFrom;
            yearFrom = yearTo;
            yearTo = tmp;
        }
        if (ratingFrom != null && ratingTo != null && ratingFrom > ratingTo) {
            Double tmp = ratingFrom;
            ratingFrom = ratingTo;
            ratingTo = tmp;
        }
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "ratingCount";
        }
        if (sortOrder == null || sortOrder.isBlank()) {
            sortOrder = "desc";
        }
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public void setYearTo(Integer yearTo) {
        this.yearTo = yearTo;
    }

    public Double getRatingFrom() {
        return ratingFrom;
    }

    public void setRatingFrom(Double ratingFrom) {
        this.ratingFrom = ratingFrom;
    }

    public Double getRatingTo() {
        return ratingTo;
    }

    public void setRatingTo(Double ratingTo) {
        this.ratingTo = ratingTo;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
