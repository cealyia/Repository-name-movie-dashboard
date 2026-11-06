package com.ctgu.movie.service;

import com.ctgu.movie.common.BusinessException;
import com.ctgu.movie.common.PageResult;
import com.ctgu.movie.common.SortWhitelist;
import com.ctgu.movie.dto.MovieDetailVO;
import com.ctgu.movie.dto.MovieQuery;
import com.ctgu.movie.dto.MovieRowVO;
import com.ctgu.movie.entity.Movie;
import com.ctgu.movie.mapper.MovieMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 影片明细业务逻辑。
 */
@Service
public class MovieService {

    private final MovieMapper movieMapper;
    private final GenreBatchLoader genreBatchLoader;

    public MovieService(MovieMapper movieMapper, GenreBatchLoader genreBatchLoader) {
        this.movieMapper = movieMapper;
        this.genreBatchLoader = genreBatchLoader;
    }

    /** 分页查询影片明细，并补全每部影片的类型 */
    public PageResult<MovieRowVO> page(MovieQuery query) {
        query.normalize();
        long total = movieMapper.countMovies(query, query.getDateFrom(), query.getDateTo());
        int offset = (query.getPage() - 1) * query.getSize();
        if (total == 0 || offset >= total) {
            return new PageResult<>(total, query.getPage(), query.getSize(), List.of());
        }
        String orderBy = SortWhitelist.resolve(query.getSortBy(), query.getSortOrder());
        List<MovieRowVO> rows = movieMapper.selectPage(query,
                query.getDateFrom(), query.getDateTo(), orderBy, offset, query.getSize());

        Map<Integer, List<String>> genres = genreBatchLoader.load(
                rows.stream().map(MovieRowVO::getMovieId).toList());
        rows.forEach(r -> r.setGenres(genres.getOrDefault(r.getMovieId(), List.of())));
        return new PageResult<>(total, query.getPage(), query.getSize(), rows);
    }

    /** 影片详情：基本信息 + 类型 + 标签 + 单部影片的月度评分趋势 */
    public MovieDetailVO detail(Integer movieId) {
        if (movieId == null) {
            throw BusinessException.badRequest("影片 ID 不能为空");
        }
        Movie movie = movieMapper.selectById(movieId);
        if (movie == null) {
            throw BusinessException.notFound("影片不存在：" + movieId);
        }
        MovieRowVO stat = movieMapper.selectStatById(movieId);

        MovieDetailVO detail = new MovieDetailVO();
        detail.setMovieId(movie.getMovieId());
        detail.setTitle(movie.getTitle());
        detail.setReleaseYear(movie.getReleaseYear());
        detail.setGenres(movieMapper.selectGenres(movieId));
        detail.setTags(movieMapper.selectTags(movieId));
        detail.setMonthly(movieMapper.selectMonthly(movieId));
        if (stat != null) {
            detail.setRatingCount(stat.getRatingCount());
            detail.setAvgScore(stat.getAvgScore());
        }
        return detail;
    }
}
