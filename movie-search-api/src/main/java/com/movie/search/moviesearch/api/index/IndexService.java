package com.movie.search.moviesearch.api.index;

import com.alibaba.fastjson.JSONObject;
import com.movie.search.moviesearch.request.IndexRequest;
import com.movie.search.moviesearch.request.QueryRequest;
import com.movie.search.moviesearch.request.UpdateDocRequest;
import com.movie.search.moviesearch.response.CommonResponse;
import com.movie.search.moviesearch.response.IndexResponse;
import com.movie.search.moviesearch.response.QueryResponse;
import com.movie.search.moviesearch.response.UpdateResponse;

public interface IndexService {
    CommonResponse<IndexResponse> creatIndex(IndexRequest request);

    CommonResponse<IndexResponse> deleteIndex(IndexRequest request);

    CommonResponse<UpdateResponse> updateOrInsertDocs(UpdateDocRequest request);

    CommonResponse<QueryResponse<JSONObject>> queryDocs(QueryRequest request);

}