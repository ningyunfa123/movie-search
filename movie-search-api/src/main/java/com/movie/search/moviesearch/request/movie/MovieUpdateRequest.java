package com.movie.search.moviesearch.request.movie;

import lombok.Data;

import java.util.List;

@Data
public class MovieUpdateRequest {
    private List<MovieParam> movieDocs;
}
