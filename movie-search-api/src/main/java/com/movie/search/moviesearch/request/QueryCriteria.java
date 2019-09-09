package com.movie.search.moviesearch.request;

import lombok.Data;

import java.util.List;

@Data
public class QueryCriteria {

    private List<Match> matches;
    private List<Term> termList;
    private List<Terms> termsList;
    private String[] includeFields;
    private List<Range> ranges;

}