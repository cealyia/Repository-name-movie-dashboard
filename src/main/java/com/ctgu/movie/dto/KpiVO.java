package com.ctgu.movie.dto;

/**
 * 顶部 KPI 卡片数据。
 * 每个指标的口径见 doc/项目文档 4.1 节，页面上的小字也会写明单位。
 */
public class KpiVO {

    /** 影片总数（部）：筛选条件下有评分的影片数 */
    private long movieCount;

    /** 评分总数（条）：筛选条件下的评分记录数 */
    private long ratingCount;

    /** 平均评分（分，10 分制）：按评分数加权的平均分 */
    private Double avgScore;

    /** 参与评分用户数（人） */
    private long userCount;

    /** 单片最高评分数（条）：评分数最多的那部影片获得的评分条数 */
    private long maxRatingCount;

    /** 有评分的影片数（部） */
    private long ratedMovieCount;

    /** 高分占比（%）：8 分及以上评分占全部评分的比例 */
    private Double highScoreRatio;

    /** 上一周期评分总数（条），用于计算环比 */
    private Long prevRatingCount;

    /** 评分数环比（%）：(本期 - 上期) / 上期 * 100，上期为 0 时返回 null */
    private Double ratingCountChange;

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

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getMaxRatingCount() {
        return maxRatingCount;
    }

    public void setMaxRatingCount(long maxRatingCount) {
        this.maxRatingCount = maxRatingCount;
    }

    public long getRatedMovieCount() {
        return ratedMovieCount;
    }

    public void setRatedMovieCount(long ratedMovieCount) {
        this.ratedMovieCount = ratedMovieCount;
    }

    public Double getHighScoreRatio() {
        return highScoreRatio;
    }

    public void setHighScoreRatio(Double highScoreRatio) {
        this.highScoreRatio = highScoreRatio;
    }

    public Long getPrevRatingCount() {
        return prevRatingCount;
    }

    public void setPrevRatingCount(Long prevRatingCount) {
        this.prevRatingCount = prevRatingCount;
    }

    public Double getRatingCountChange() {
        return ratingCountChange;
    }

    public void setRatingCountChange(Double ratingCountChange) {
        this.ratingCountChange = ratingCountChange;
    }
}
