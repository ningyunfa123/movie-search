package com.movie.search.moviesearch.request;

import lombok.Data;

@Data
public class Range {
    private String key;
    private Object from;
    private Object to;
    private String relative;
}
