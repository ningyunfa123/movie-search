package com.movie.search.moviesearch.exceptionhandler;

public class BusinessException extends RuntimeException{
    private Long errno;
    public BusinessException(Long errno, String msg) {
        super(msg);
        this.errno = errno;
    }

    public void setErrno(Long errno) {
        this.errno = errno;
    }

    public Long getErrno() {
        return errno;
    }
}
