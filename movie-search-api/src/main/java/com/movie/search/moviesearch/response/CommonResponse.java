package com.movie.search.moviesearch.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonResponse<T> {
    private Long errno;
    private String msg;
    private T data;
}
