package com.movie.search.moviesearch.response;


import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class UpdateResponse {

    private List<String> faildDocs;
    private Integer count;
}
