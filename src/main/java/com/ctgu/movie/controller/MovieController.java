package com.ctgu.movie.controller;

import com.ctgu.movie.common.PageResult;
import com.ctgu.movie.common.Result;
import com.ctgu.movie.dto.MovieDetailVO;
import com.ctgu.movie.dto.MovieQuery;
import com.ctgu.movie.dto.MovieRowVO;
import com.ctgu.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 影片明细接口。
 */
@RestController
@RequestMapping("/api/movies")
@Tag(name = "影片接口", description = "影片分页明细与单片详情")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(summary = "影片分页列表", description = "支持类型、上映年份、评分区间、评分时间、关键字筛选与排序")
    @GetMapping
    public Result<PageResult<MovieRowVO>> page(@Valid @ModelAttribute MovieQuery query) {
        return Result.ok(movieService.page(query));
    }

    @Operation(summary = "影片详情", description = "返回基本信息、类型、标签与该片按月评分趋势")
    @GetMapping("/{id}")
    public Result<MovieDetailVO> detail(@PathVariable Integer id) {
        return Result.ok(movieService.detail(id));
    }
}
