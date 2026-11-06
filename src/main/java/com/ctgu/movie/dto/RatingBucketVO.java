package com.ctgu.movie.dto;

/**
 * 评分区间分布：一个评分区间内有多少部影片。
 */
public class RatingBucketVO {

    /** 评分区间标签，例如 8.0-8.9 */
    private String bucket;

    /** 落在该区间的影片数（部） */
    private long movieCount;


    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public long getMovieCount() {
        return movieCount;
    }

    public void setMovieCount(long movieCount) {
        this.movieCount = movieCount;
    }
}
