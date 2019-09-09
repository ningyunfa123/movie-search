package com.movie.search.moviesearch.response.movie;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MovieQueryResponse {
    private Integer size;
    private Long totalNum;
    private List<MovieDoc> docs;

}
