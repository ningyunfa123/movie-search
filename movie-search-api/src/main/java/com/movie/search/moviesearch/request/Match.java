package com.movie.search.moviesearch.request;

import lombok.Data;

@Data
public class Match {
    private String key;
    private String value;
    private String relative;
}
