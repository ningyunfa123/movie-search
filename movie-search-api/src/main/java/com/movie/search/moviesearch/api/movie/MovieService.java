package com.movie.search.moviesearch.api.movie;

import com.movie.search.moviesearch.request.movie.MovieQueryRequest;
import com.movie.search.moviesearch.request.movie.MovieUpdateRequest;
import com.movie.search.moviesearch.response.CommonResponse;
import com.movie.search.moviesearch.response.UpdateResponse;
import com.movie.search.moviesearch.response.movie.MovieQueryResponse;

public interface MovieService {

    CommonResponse<MovieQueryResponse> queryMovie(MovieQueryRequest request);

    CommonResponse<UpdateResponse> updateMovieDocs(MovieUpdateRequest request);
}
