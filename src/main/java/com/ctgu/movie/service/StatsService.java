package com.ctgu.movie.service;

import com.ctgu.movie.common.BusinessException;
import com.ctgu.movie.dto.FilterMetaVO;
import com.ctgu.movie.dto.GenreStatVO;
import com.ctgu.movie.dto.HeatCellVO;
import com.ctgu.movie.dto.KpiVO;
import com.ctgu.movie.dto.MovieQuery;
import com.ctgu.movie.dto.RatingBucketVO;
import com.ctgu.movie.dto.TagVO;
import com.ctgu.movie.dto.TopMovieVO;
import com.ctgu.movie.dto.TrendPointVO;
import com.ctgu.movie.mapper.StatsMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 统计类业务逻辑：
 * 1) 校验前端传来的维度/排序参数（防止乱传值把 SQL 弄错）；
 * 2) 调用 Mapper 的聚合 SQL；
 * 3) 组装 KPI（含环比）、补全 TOP 列表的类型。
 */
@Service
public class StatsService {

    /** 评分区间固定顺序，保证饼图/柱图颜色和图例稳定 */
    private static final List<String> BUCKET_ORDER =
            List.of("9.0-10", "8.0-8.9", "7.0-7.9", "6.0-6.9", "0-5.9");

    private final StatsMapper statsMapper;
    private final GenreBatchLoader genreBatchLoader;

    public StatsService(StatsMapper statsMapper, GenreBatchLoader genreBatchLoader) {
        this.statsMapper = statsMapper;
        this.genreBatchLoader = genreBatchLoader;
    }

    /** 看板初始化数据：类型列表、年份范围、日期范围、总记录数 */
    public FilterMetaVO meta() {
        FilterMetaVO meta = new FilterMetaVO();
        meta.setGenres(statsMapper.selectGenres());
        meta.setYearMin(statsMapper.selectMinYear());
        meta.setYearMax(statsMapper.selectMaxYear());
        meta.setDateMin(statsMapper.selectMinDate());
        meta.setDateMax(statsMapper.selectMaxDate());
        meta.setTotalMovies(statsMapper.countAllMovies());
        meta.setTotalRatings(statsMapper.countAllRatings());
        meta.setTotalUsers(statsMapper.countAllUsers());
        meta.setTotalTags(statsMapper.countAllTags());
        return meta;
    }

    /** KPI 卡片：在 SQL 聚合结果的基础上补用户数和环比 */
    public KpiVO overview(MovieQuery query) {
        query.normalize();
        LocalDate from = query.getDateFrom();
        LocalDate to = query.getDateTo();

        KpiVO kpi = statsMapper.selectKpi(query, from, to);
        if (kpi == null) {
            kpi = new KpiVO();
        }
        kpi.setUserCount(statsMapper.countUsers(query, from, to));

        // 环比：取「与当前区间等长、紧挨着的上一段区间」
        long days = ChronoUnit.DAYS.between(from, to) + 1;
        LocalDate prevTo = from.minusDays(1);
        LocalDate prevFrom = prevTo.minusDays(days - 1);
        if (!prevFrom.isBefore(LocalDate.of(1900, 1, 2))) {
            long prev = statsMapper.countRatings(query, prevFrom, prevTo);
            kpi.setPrevRatingCount(prev);
            if (prev > 0) {
                double change = (kpi.getRatingCount() - prev) * 100.0 / prev;
                kpi.setRatingCountChange(Math.round(change * 10) / 10.0);
            }
        }
        return kpi;
    }

    /** 评分趋势：granularity 只允许 day / month */
    public List<TrendPointVO> trend(MovieQuery query, String granularity) {
        query.normalize();
        String g = "day".equalsIgnoreCase(granularity) ? "day" : "month";
        return statsMapper.selectTrend(query, query.getDateFrom(), query.getDateTo(), g);
    }

    public List<GenreStatVO> genreStats(MovieQuery query) {
        query.normalize();
        return statsMapper.selectGenreStats(query, query.getDateFrom(), query.getDateTo());
    }

    /** 评分区间分布，按 9-10、8-9 …… 的顺序返回，前端图例不会乱跳 */
    public List<RatingBucketVO> ratingDistribution(MovieQuery query) {
        query.normalize();
        List<RatingBucketVO> rows =
                statsMapper.selectRatingDistribution(query, query.getDateFrom(), query.getDateTo());
        List<RatingBucketVO> ordered = new ArrayList<>();
        for (String bucket : BUCKET_ORDER) {
            rows.stream()
                    .filter(r -> bucket.equals(r.getBucket()))
                    .findFirst()
                    .ifPresent(ordered::add);
        }
        return ordered;
    }

    /** TOP N：metric 只允许 ratingCount / avgScore */
    public List<TopMovieVO> top(MovieQuery query, String metric, int limit) {
        query.normalize();
        int safeLimit = Math.max(1, Math.min(limit, 50));
        String orderBy = "avgScore".equalsIgnoreCase(metric)
                ? "avg_score DESC, rating_count DESC"
                : "rating_count DESC, avg_score DESC";
        List<TopMovieVO> rows = statsMapper.selectTop(
                query, query.getDateFrom(), query.getDateTo(), orderBy, safeLimit);
        Map<Integer, List<String>> genres = genreBatchLoader.load(
                rows.stream().map(TopMovieVO::getMovieId).toList());
        rows.forEach(r -> r.setGenres(genres.getOrDefault(r.getMovieId(), List.of())));
        return rows;
    }

    /** 词云标签 */
    public List<TagVO> tags(MovieQuery query, int limit) {
        query.normalize();
        int safeLimit = Math.max(10, Math.min(limit, 100));
        return statsMapper.selectTags(query, query.getDateFrom(), query.getDateTo(), safeLimit);
    }

    /** 年代 × 类型 热力图，按年代升序返回 */
    public List<HeatCellVO> heatmap(MovieQuery query) {
        query.normalize();
        List<HeatCellVO> rows = statsMapper.selectHeatmap(query, query.getDateFrom(), query.getDateTo());
        rows.sort(Comparator.comparing(HeatCellVO::getDecade,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return rows;
    }

    /** 供 Controller 校验 granularity 用 */
    public void checkGranularity(String granularity) {
        if (granularity != null && !granularity.isBlank()
                && !"day".equalsIgnoreCase(granularity)
                && !"month".equalsIgnoreCase(granularity)) {
            throw BusinessException.badRequest("granularity 只支持 day 或 month");
        }
    }
}
