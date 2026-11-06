package com.ctgu.movie.service;

import com.ctgu.movie.dto.MovieGenreVO;
import com.ctgu.movie.mapper.MovieMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量补全影片类型。
 * 如果不这样做，一页 10 部影片就要查 10 次类型表（N+1 查询），
 * 这里改成一条 IN 查询，再在内存里分组。
 */
@Component
public class GenreBatchLoader {

    private final MovieMapper movieMapper;

    public GenreBatchLoader(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    public Map<Integer, List<String>> load(Collection<Integer> movieIds) {
        Map<Integer, List<String>> result = new HashMap<>();
        if (movieIds == null || movieIds.isEmpty()) {
            return result;
        }
        List<MovieGenreVO> rows = movieMapper.selectGenresByMovieIds(new ArrayList<>(movieIds));
        for (MovieGenreVO row : rows) {
            result.computeIfAbsent(row.getMovieId(), k -> new ArrayList<>()).add(row.getGenreName());
        }
        return result;
    }
}
