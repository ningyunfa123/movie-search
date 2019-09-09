package com.movie.search.moviesearch.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QueryResponse<T> {
    List<T> docs;
    Long totalNum;
    Integer size;
}
