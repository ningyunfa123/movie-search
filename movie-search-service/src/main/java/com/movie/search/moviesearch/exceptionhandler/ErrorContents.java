package com.movie.search.moviesearch.exceptionhandler;

public enum  ErrorContents {
    SYSTEM_ERROR(1000L, "系统异常"),
    INDEX_EXIST(1001L, "索引已存在"),
    INDEX_CREATE_FAILD(1002L, "索引创建失败/索引已存在"),
    INDEX_MAPPING_FAILD(1003L, "索引映射构建失败"),
    INDEX_DELETE_FAILD(1004L, "索引删除失败"),
    QUERY_INDEX_FAILED(1005L, "数据召回失败"),
    DATA_TRANS_ERROR(1006L, "数据转换失败"),
    EMPTY_DOC(1007L, "空文档");
    private Long errno;
    private String msg;
    ErrorContents(Long errno, String msg){
        this.errno = errno;
        this.msg = msg;
    }
    public Long getErrno() {
        return errno;
    }
    public String getMsg() {
        return msg;
    }

    public void setErrno(Long errno) {
        this.errno = errno;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
