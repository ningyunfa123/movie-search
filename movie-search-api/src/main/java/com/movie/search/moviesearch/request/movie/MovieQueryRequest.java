package com.movie.search.moviesearch.request.movie;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MovieQueryRequest {

    private String years;
    private String countries;
    private Float comment;
    private String category;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private String name;
    private String language;
}
