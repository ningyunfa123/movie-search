package com.movie.search.moviesearch.exceptionhandler;

import com.movie.search.moviesearch.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class CommonExceptionHandler<T> {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResponse<T> handleException(Exception e, T resp) {
        log.error("system exception, exception class {}", e.getClass().toString());

        return CommonResponse.<T>builder().errno(ErrorContents.SYSTEM_ERROR.getErrno()).msg(ErrorContents.SYSTEM_ERROR.getMsg()).data(resp).build();
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public CommonResponse<T> handleBusinessException(BusinessException e, T response) {
        log.error("biz exception {}", e.getClass().toString());
        return CommonResponse.<T>builder().errno(e.getErrno()).msg(e.getMessage()).data(response).build();
    }

}
