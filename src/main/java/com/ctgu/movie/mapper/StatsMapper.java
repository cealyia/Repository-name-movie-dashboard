package com.ctgu.movie.mapper;

import com.ctgu.movie.dto.GenreStatVO;
import com.ctgu.movie.dto.HeatCellVO;
import com.ctgu.movie.dto.KpiVO;
import com.ctgu.movie.dto.MovieQuery;
import com.ctgu.movie.dto.RatingBucketVO;
import com.ctgu.movie.dto.TagVO;
import com.ctgu.movie.dto.TopMovieVO;
import com.ctgu.movie.dto.TrendPointVO;
import com.ctgu.movie.entity.Genre;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计类查询。SQL 全部写在 resources/mapper/StatsMapper.xml 中。
 * 参数里的 from / to 是「评分时间范围」，和筛选条件 q 一起参与聚合。
 */
public interface StatsMapper {

    /** 顶部 KPI（不含用户数、环比，这两项单独查） */
    KpiVO selectKpi(@Param("q") MovieQuery q,
                    @Param("from") LocalDate from,
                    @Param("to") LocalDate to);

    /** 某个时间范围内的评分数 */
    long countRatings(@Param("q") MovieQuery q,
                      @Param("from") LocalDate from,
                      @Param("to") LocalDate to);

    /** 某个时间范围内的去重用户数 */
    long countUsers(@Param("q") MovieQuery q,
                    @Param("from") LocalDate from,
                    @Param("to") LocalDate to);

    /** 评分趋势，granularity = day / month */
    List<TrendPointVO> selectTrend(@Param("q") MovieQuery q,
                                   @Param("from") LocalDate from,
                                   @Param("to") LocalDate to,
                                   @Param("granularity") String granularity);

    /** 类型维度统计：影片数、评分数、平均分 */
    List<GenreStatVO> selectGenreStats(@Param("q") MovieQuery q,
                                       @Param("from") LocalDate from,
                                       @Param("to") LocalDate to);

    /** 影片平均分的区间分布 */
    List<RatingBucketVO> selectRatingDistribution(@Param("q") MovieQuery q,
                                                  @Param("from") LocalDate from,
                                                  @Param("to") LocalDate to);

    /** TOP N 排行，orderBy 由 Service 白名单校验后传入 */
    List<TopMovieVO> selectTop(@Param("q") MovieQuery q,
                               @Param("from") LocalDate from,
                               @Param("to") LocalDate to,
                               @Param("orderBy") String orderBy,
                               @Param("limit") int limit);

    /** 标签词频 */
    List<TagVO> selectTags(@Param("q") MovieQuery q,
                           @Param("from") LocalDate from,
                           @Param("to") LocalDate to,
                           @Param("limit") int limit);

    /** 年代 × 类型 的影片数热力图数据 */
    List<HeatCellVO> selectHeatmap(@Param("q") MovieQuery q,
                                   @Param("from") LocalDate from,
                                   @Param("to") LocalDate to);

    /** 初始化元数据：全部类型 */
    List<Genre> selectGenres();

    Integer selectMinYear();

    Integer selectMaxYear();

    LocalDate selectMinDate();

    LocalDate selectMaxDate();

    long countAllMovies();

    long countAllRatings();

    long countAllUsers();

    long countAllTags();
}
