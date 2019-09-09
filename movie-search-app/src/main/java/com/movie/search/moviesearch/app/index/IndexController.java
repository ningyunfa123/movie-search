package com.movie.search.moviesearch.app.index;

import com.movie.search.moviesearch.api.index.IndexService;
import com.movie.search.moviesearch.exceptionhandler.BusinessException;
import com.movie.search.moviesearch.exceptionhandler.CommonExceptionHandler;
import com.movie.search.moviesearch.request.IndexRequest;
import com.movie.search.moviesearch.response.CommonResponse;
import com.movie.search.moviesearch.response.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("movie-search/index")
public class IndexController {

    @Autowired
    private IndexService indexService;
    @Autowired
    private CommonExceptionHandler commonExceptionHandler;

    @RequestMapping("/create-index")
    @ResponseBody
    public CommonResponse<IndexResponse> createIndex(@RequestBody @Valid IndexRequest request) {
        try {
            return indexService.creatIndex(request);
        } catch (BusinessException e) {
            return commonExceptionHandler.handleBusinessException(e, IndexResponse.builder().status(500).build());
        } catch (Exception e) {
            return commonExceptionHandler.handleException(e, IndexResponse.builder().status(500).build());
        }
    }

    @RequestMapping("/delete-index")
    @ResponseBody
    public CommonResponse<IndexResponse> deleteIndex(@RequestBody @Valid IndexRequest request) {
        try {
            return indexService.deleteIndex(request);
        } catch (BusinessException e) {
            return commonExceptionHandler.handleBusinessException(e, IndexResponse.builder().status(500).build());
        }
    }
}
