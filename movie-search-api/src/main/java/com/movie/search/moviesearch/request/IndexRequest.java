package com.movie.search.moviesearch.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class IndexRequest {
    @NotEmpty
    private String index;

    @NotEmpty
    private String type;

    private List<IndexColumn> indexColumns;
}
