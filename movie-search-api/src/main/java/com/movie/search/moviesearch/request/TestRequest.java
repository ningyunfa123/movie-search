package com.movie.search.moviesearch.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestRequest {
    private String movie_name;
    private Integer year;
    private String description;
    private Float comment;
    private String category;
}
