package com.ctgu.movie.controller;

import com.ctgu.movie.common.Result;
import com.ctgu.movie.dto.FilterMetaVO;
import com.ctgu.movie.dto.GenreStatVO;
import com.ctgu.movie.dto.HeatCellVO;
import com.ctgu.movie.dto.KpiVO;
import com.ctgu.movie.dto.MovieQuery;
import com.ctgu.movie.dto.RatingBucketVO;
import com.ctgu.movie.dto.TagVO;
import com.ctgu.movie.dto.TopMovieVO;
import com.ctgu.movie.dto.TrendPointVO;
import com.ctgu.movie.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计类接口。全部是查询，所以统一用 GET。
 * 路径设计：/api/stats/xxx 表示聚合统计，/api/movies 表示明细数据。
 */
@RestController
@RequestMapping("/api")
@Tag(name = "统计接口", description = "KPI、趋势、类型对比、分布、排行、词云、热力图")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @Operation(summary = "筛选元数据", description = "返回全部类型、年份范围、日期范围与总记录数，供前端初始化筛选器")
    @GetMapping("/meta/filters")
    public Result<FilterMetaVO> filters() {
        return Result.ok(statsService.meta());
    }

    @Operation(summary = "顶部 KPI", description = "影片数、评分数、平均分、用户数、极值、高分占比、环比")
    @GetMapping("/stats/overview")
    public Result<KpiVO> overview(@Valid @ModelAttribute MovieQuery query) {
        return Result.ok(statsService.overview(query));
    }

    @Operation(summary = "评分趋势", description = "按天或按月聚合评分条数与平均分")
    @GetMapping("/stats/trend")
    public Result<List<TrendPointVO>> trend(@Valid @ModelAttribute MovieQuery query,
                                            @RequestParam(defaultValue = "month") String granularity) {
        statsService.checkGranularity(granularity);
        return Result.ok(statsService.trend(query, granularity));
    }

    @Operation(summary = "类型对比", description = "每个类型的影片数、评分数、平均分")
    @GetMapping("/stats/genres")
    public Result<List<GenreStatVO>> genres(@Valid @ModelAttribute MovieQuery query) {
        return Result.ok(statsService.genreStats(query));
    }

    @Operation(summary = "评分区间分布", description = "按影片平均分为 5 档，统计各档影片数")
    @GetMapping("/stats/rating-distribution")
    public Result<List<RatingBucketVO>> ratingDistribution(@Valid @ModelAttribute MovieQuery query) {
        return Result.ok(statsService.ratingDistribution(query));
    }

    @Operation(summary = "TOP N 排行", description = "metric=ratingCount 按评分数排行，metric=avgScore 按平均分排行")
    @GetMapping("/stats/top")
    public Result<List<TopMovieVO>> top(@Valid @ModelAttribute MovieQuery query,
                                        @RequestParam(defaultValue = "ratingCount") String metric,
                                        @RequestParam(defaultValue = "10") int limit) {
        return Result.ok(statsService.top(query, metric, limit));
    }

    @Operation(summary = "标签词云", description = "按标签出现次数取前 N 个")
    @GetMapping("/stats/tags")
    public Result<List<TagVO>> tags(@Valid @ModelAttribute MovieQuery query,
                                    @RequestParam(defaultValue = "60") int limit) {
        return Result.ok(statsService.tags(query, limit));
    }

    @Operation(summary = "年代-类型热力图", description = "每个年代每个类型的影片数")
    @GetMapping("/stats/heatmap")
    public Result<List<HeatCellVO>> heatmap(@Valid @ModelAttribute MovieQuery query) {
        return Result.ok(statsService.heatmap(query));
    }
}
