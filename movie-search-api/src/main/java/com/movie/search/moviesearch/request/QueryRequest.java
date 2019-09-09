package com.movie.search.moviesearch.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class QueryRequest {
    private Integer from = 0;
    private Integer size = 20;
    @NotEmpty
    private String indexName;
    @NotEmpty
    private String indexType;
    private QueryCriteria criteria;
}
