package com.movie.search.moviesearch.request;

import lombok.Data;

import java.util.Collection;

@Data
public class Terms {
    private String key;
    private Collection<String> values;
    private String relative;
}
