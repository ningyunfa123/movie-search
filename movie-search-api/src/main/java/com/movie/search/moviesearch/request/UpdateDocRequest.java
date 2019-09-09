package com.movie.search.moviesearch.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateDocRequest {
    @NotEmpty
    private String indexName;
    @NotEmpty
    private String indexType;
    @NotNull
    private List<DocParam> docs;
}
