package com.ctgu.movie.mapper;

import com.ctgu.movie.dto.MovieGenreVO;
import com.ctgu.movie.dto.MovieQuery;
import com.ctgu.movie.dto.MovieRowVO;
import com.ctgu.movie.dto.TrendPointVO;
import com.ctgu.movie.entity.Movie;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 影片明细相关查询。SQL 写在 resources/mapper/MovieMapper.xml。
 * from / to 与统计接口保持一致，都是「评分时间范围」。
 */
public interface MovieMapper {

    /** 分页查询影片列表 */
    List<MovieRowVO> selectPage(@Param("q") MovieQuery q,
                                @Param("from") LocalDate from,
                                @Param("to") LocalDate to,
                                @Param("orderBy") String orderBy,
                                @Param("offset") int offset,
                                @Param("size") int size);

    /** 满足筛选条件的影片总数 */
    long countMovies(@Param("q") MovieQuery q,
                     @Param("from") LocalDate from,
                     @Param("to") LocalDate to);

    /** 批量查询影片的类型，避免逐条查询造成 N+1 */
    List<MovieGenreVO> selectGenresByMovieIds(@Param("ids") List<Integer> ids);

    /** 按 ID 查影片 */
    Movie selectById(@Param("id") Integer id);

    /** 单部影片的评分数与平均分 */
    MovieRowVO selectStatById(@Param("id") Integer id);

    /** 单部影片的类型 */
    List<String> selectGenres(@Param("id") Integer id);

    /** 单部影片的标签（最多 20 个） */
    List<String> selectTags(@Param("id") Integer id);

    /** 单部影片按月聚合的评分趋势 */
    List<TrendPointVO> selectMonthly(@Param("id") Integer id);
}
