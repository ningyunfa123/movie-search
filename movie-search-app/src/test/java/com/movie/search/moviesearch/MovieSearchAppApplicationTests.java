package com.movie.search.moviesearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.movie.search.moviesearch.api.index.IndexService;
import com.movie.search.moviesearch.api.movie.MovieService;
import com.movie.search.moviesearch.common.RedisService;
import com.movie.search.moviesearch.request.*;
import com.movie.search.moviesearch.request.movie.MovieQueryRequest;
import com.movie.search.moviesearch.request.movie.TestUpdateDocRequest;
import com.movie.search.moviesearch.response.CommonResponse;
import com.movie.search.moviesearch.response.IndexResponse;
import com.movie.search.moviesearch.response.QueryResponse;
import com.movie.search.moviesearch.response.UpdateResponse;
import com.movie.search.moviesearch.response.movie.MovieQueryResponse;
import io.searchbox.client.JestClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MovieSearchAppApplicationTests {

    @Autowired
    private RedisService<Object, Object> redisService;
    @Autowired
    private JestClient jestClient;

    @Autowired
    private IndexService indexService;

    @Autowired
    private MovieService movieService;

    @Test
    public void contextLoads() {
        String key = "redisTest";
        redisService.set(key, "123444");
        System.out.println(redisService.get(key));
    }

        @Test
        public void testCreateIndex(){
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.setIndex("mytest1");
            indexRequest.setType("mytest1");
            List<IndexColumn> columnList = new ArrayList<>();
            IndexColumn indexColumn = new IndexColumn();
            indexColumn.setKey("id");
            indexColumn.setType("long");
            columnList.add(indexColumn);

            indexColumn = new IndexColumn();
            indexColumn.setKey("price");
            indexColumn.setType("long");
            columnList.add(indexColumn);

            indexColumn = new IndexColumn();
            indexColumn.setKey("name");
            indexColumn.setType("text");
            Map<String, Object> map = new HashMap<>();
            map.put("analyser", "ik_max_word");
            map.put("index", true);
            indexColumn.setProperties(map);
            columnList.add(indexColumn);
            indexRequest.setIndexColumns(columnList);

            CommonResponse<IndexResponse> response = indexService.creatIndex(indexRequest);
            log.info("response:{}", JSON.toJSONString(response));
    }

    @Test
    public void testDeleteIndex() {
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.setIndex("mytest3");
        indexRequest.setType("mytest3");
        CommonResponse<IndexResponse> response = indexService.deleteIndex(indexRequest);
        log.info("response:{}", JSON.toJSONString(response));
    }

    @Test
    public void testUpdateOrInsertDocs() {
        UpdateDocRequest updateDocRequest = new UpdateDocRequest();
        updateDocRequest.setIndexName("movie_index_test");
        updateDocRequest.setIndexType("movie_index_test");
        DocParam docParam = new DocParam();
        docParam.setIndexId("长江七号");
        TestRequest testRequest = TestRequest.builder().category("爱情/科幻").comment(7.5F).description("金三顺").movie_name("我叫金三顺").year(2018).build();
        TestUpdateDocRequest testUpdateDocRequest = TestUpdateDocRequest.builder().category("爱情/科幻").comment(6.7F).country("欧美")
                .description("简介").director("张艺谋").downloadLink("http://baidu.com").image("http://image.com").language("英语")
                .name("长江七号").openTime("2018-07-16 00:00:00").subName("张江七号英雄归来").year(2018).build();
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(testUpdateDocRequest));
        docParam.setDoc(jsonObject);
        List<DocParam> docList = new ArrayList<>();
        docList.add(docParam);
        updateDocRequest.setDocs(docList);
        CommonResponse<UpdateResponse> response = indexService.updateOrInsertDocs(updateDocRequest);
        log.info("response:{}", JSON.toJSONString(response));


    }

    @Test
    public void testQueryDocs() {
        QueryRequest request = new QueryRequest();
        request.setIndexName("mytest2");
        request.setIndexType("mytest2");
        QueryCriteria criteria = new QueryCriteria();
        List<Match> matches = new ArrayList<>();
        Match match = new Match();
        match.setKey("category");
        match.setValue("恐怖");
        match.setRelative("or");
//        matches.add(match);
        match = new Match();
        match.setKey("category");
        match.setValue("恐怖动作");
        match.setRelative("or");
        matches.add(match);
//        criteria.setMatches(matches);

        List<Range> ranges = new ArrayList<>();
        Range range = new Range();
        range.setKey("comment");
        range.setFrom(8.5);
        ranges.add(range);
        criteria.setRanges(ranges);

        List<Terms> termsList = new ArrayList<>();
        Terms terms = new Terms();
        terms.setKey("year");
        terms.setRelative("and");
        List<String> years = new ArrayList<>();
        years.add("2017");
        terms.setValues(years);
        request.setCriteria(criteria);
        termsList.add(terms);
        criteria.setTermsList(termsList);
        CommonResponse<QueryResponse<JSONObject>> resp = indexService.queryDocs(request);
        log.info("resp:{}", JSON.toJSONString(resp));
    }

    @Test
    public void testMovieQuery() {
        MovieQueryRequest request = new MovieQueryRequest();
        request.setYears("2018");
        request.setCategory("科幻");
        CommonResponse<MovieQueryResponse> response = movieService.queryMovie(request);
        log.info("resp:{}", JSON.toJSONString(response));
    }

}
