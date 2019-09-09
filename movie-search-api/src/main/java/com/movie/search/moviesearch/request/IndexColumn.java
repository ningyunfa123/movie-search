package com.movie.search.moviesearch.request;

import lombok.Data;

import java.util.Map;

@Data
public class IndexColumn {
    private String key;
    private Map<String, Object> properties;
    private String type;

    private String store;//yes no
    private String analyzer;//ik_max_word
    private String index;//analyzed not_analyzed
    private Boolean mainKey;//1 0
}
