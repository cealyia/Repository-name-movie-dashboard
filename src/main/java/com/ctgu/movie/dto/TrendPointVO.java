package com.ctgu.movie.dto;

/**
 * 趋势图上的一个点：一个时间粒度（月/日）内的评分条数与平均分。
 */
public class TrendPointVO {

    /** 时间粒度标签，例如 2015-06 或 2015-06-01 */
    private String period;

    /** 该时间粒度内的评分条数 */
    private long ratingCount;

    /** 该时间粒度内的平均评分（10 分制） */
    private Double avgScore;


    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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
