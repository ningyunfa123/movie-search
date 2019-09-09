package com.movie.search.moviesearch.app.movie;

import com.movie.search.moviesearch.api.movie.MovieService;
import com.movie.search.moviesearch.exceptionhandler.BusinessException;
import com.movie.search.moviesearch.exceptionhandler.CommonExceptionHandler;
import com.movie.search.moviesearch.request.movie.MovieQueryRequest;
import com.movie.search.moviesearch.request.movie.MovieUpdateRequest;
import com.movie.search.moviesearch.response.CommonResponse;
import com.movie.search.moviesearch.response.UpdateResponse;
import com.movie.search.moviesearch.response.movie.MovieQueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("movie-search/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private CommonExceptionHandler exceptionHandler;

    @RequestMapping("/query-movie")
    @ResponseBody
    public CommonResponse<MovieQueryResponse> queryMovies(MovieQueryRequest request) {
        try{
            return movieService.queryMovie(request);
        }catch (BusinessException e) {
            return exceptionHandler.handleBusinessException(e, null);
        }catch (Exception e) {
            return exceptionHandler.handleException(e, null);
        }
    }

    @RequestMapping("/update-movie")
    @ResponseBody
    public CommonResponse<UpdateResponse> updateMovieDoc(@RequestBody MovieUpdateRequest request) {
        try {
            return movieService.updateMovieDocs(request);
        }catch (BusinessException e) {
            return exceptionHandler.handleBusinessException(e, null);
        }catch (Exception e) {
            return exceptionHandler.handleException(e, null);
        }
    }
}
