package com.movie.search.moviesearch.request.movie;

import lombok.Data;

@Data
public class MovieParam {
    private String title;
    private String name;
    private String subName;
    private Integer year;
    private String country;
    private String category;
    private String language;
    private String openTime;
    private Float comment;
    private String director;
    private String description;
    private String image;
    private String downloadLink;
    private String actor;
}

