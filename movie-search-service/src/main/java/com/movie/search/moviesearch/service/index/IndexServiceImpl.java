package com.movie.search.moviesearch.service.index;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonArray;
import com.movie.search.moviesearch.api.index.IndexService;
import com.movie.search.moviesearch.common.RedisService;
import com.movie.search.moviesearch.exceptionhandler.BusinessException;
import com.movie.search.moviesearch.exceptionhandler.CommonExceptionHandler;
import com.movie.search.moviesearch.exceptionhandler.ErrorContents;
import com.movie.search.moviesearch.request.*;
import com.movie.search.moviesearch.response.CommonResponse;
import com.movie.search.moviesearch.response.IndexResponse;
import com.movie.search.moviesearch.response.QueryResponse;
import com.movie.search.moviesearch.response.UpdateResponse;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.PutMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IndexServiceImpl implements IndexService {

    @Autowired
    private JestClient jestClient;

    @Autowired
    private RedisService<String, String> redisService;

    @Override
    public CommonResponse<IndexResponse> creatIndex(IndexRequest request) {
        String key = request.getIndex() + "_" + request.getType();
        if (!StringUtils.isEmpty(redisService.get(key))) {
            throw new BusinessException(ErrorContents.INDEX_EXIST.getErrno(), ErrorContents.INDEX_EXIST.getMsg());
        }
        JestResult result = null;
        try {
            CreateIndex.Builder createIndexBuilder = new CreateIndex.Builder(request.getIndex()).settings(getPutMapping(request.getIndexColumns(), request.getType()));
//            PutMapping.Builder putMapping = new PutMapping.Builder(request.getIndex(), request.getType(),
//                    getPutMapping(request.getIndexColumns(), request.getType()));
//            putMapping.setHeader(PWDKEY, getSecret());
//            putMapping.refresh(true);
            result = jestClient.execute(createIndexBuilder.build());
//            mapResult = jestClient.execute(putMapping.build());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorContents.INDEX_CREATE_FAILD.getErrno(), ErrorContents.INDEX_CREATE_FAILD.getMsg());
        }
        if (result == null || !result.isSucceeded()) {
            throw new BusinessException(ErrorContents.INDEX_CREATE_FAILD.getErrno(), ErrorContents.INDEX_CREATE_FAILD.getMsg());
        }
        try {
            redisService.set(key, getPutMapping(request.getIndexColumns(), request.getType()));
        } catch (Exception e) {
            log.error("index saves redis faild");
            e.printStackTrace();
        }
        return CommonResponse.<IndexResponse>builder().errno(0L).msg("SUCCESS")
                .data(IndexResponse.builder().status(200).build()).build();
    }

    @Override
    public CommonResponse<IndexResponse> deleteIndex(IndexRequest request) {
        DeleteIndex.Builder builder = new DeleteIndex.Builder(request.getIndex());
        String key = request.getIndex() + "_" + request.getType();
        try {
            redisService.delete(key);
            jestClient.execute(builder.build());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("index delete faild:{}", e.getMessage());
            throw new BusinessException(ErrorContents.INDEX_DELETE_FAILD.getErrno(), ErrorContents.INDEX_DELETE_FAILD.getMsg());
        }
        return CommonResponse.<IndexResponse>builder().errno(0L)
                .msg("SUCCESS").data(IndexResponse.builder().status(200).build()).build();
    }

    @Override
    public CommonResponse<UpdateResponse> updateOrInsertDocs(UpdateDocRequest request) {
        List<String> faildDocs = new ArrayList<>();
        Integer count = 0;
        for (DocParam doc : request.getDocs()) {
            Index.Builder builder = new Index.Builder(doc.getDoc()).id(doc.getIndexId()).index(request.getIndexName()).type(request.getIndexType());
            JestResult result = null;
            try {
                result = jestClient.execute(builder.build());
            } catch (IOException e) {
                log.error("doc update failed:{}", JSON.toJSONString(doc.getDoc()));
                e.printStackTrace();
                faildDocs.add(doc.getIndexId());
                continue;
            }
            if (result == null || !result.isSucceeded()) {
                faildDocs.add(doc.getIndexId());
                log.error("doc update failed:{}", JSON.toJSONString(doc.getDoc()));
            }
            count++;
        }
        return CommonResponse.<UpdateResponse>builder().errno(0L).msg("SUCCESS")
                .data(UpdateResponse.builder().faildDocs(faildDocs).count(count).build()).build();
    }

    @Override
    public CommonResponse<QueryResponse<JSONObject>> queryDocs(QueryRequest request) {
        String query = buildQuery(request, request.getCriteria());
//        query = "{\"query\":{\"bool\":{\"must\":{\"match\":{\"movie_name\":\"测试\"}}}}";
        log.info("docQuery:" + query);
        Search.Builder searchBuilder = new Search.Builder(query).addIndex(request.getIndexName()).addType(request.getIndexType());
        try {
            SearchResult searchResult = jestClient.execute(searchBuilder.build());
            if (!searchResult.isSucceeded()) {
                throw new BusinessException(ErrorContents.QUERY_INDEX_FAILED.getErrno(), ErrorContents.QUERY_INDEX_FAILED.getMsg());
            }
            List<SearchResult.Hit<JSONObject, Void>> hits = searchResult.getHits(JSONObject.class);
            log.info("hits:{}", JSON.toJSONString(hits));
            List<JSONObject> hitList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(hits)) {
                hits.forEach(hit -> {
                    JSONObject source = hit.source;
                    source.put("_id", hit.id);
                    source.put("_score", hit.score);
                    if (MapUtils.isNotEmpty(hit.highlight)) {
                        source.put("highlight", hit.highlight);
                    }
                    if (!CollectionUtils.isEmpty(hit.sort)) {
                        source.put("sort", hit.sort);
                    }
                    hitList.add(source);
                });
            }
            return CommonResponse.<QueryResponse<JSONObject>>builder().errno(0L).msg("SUCCESS")
                    .data(QueryResponse.<JSONObject>builder().size(hitList.size()).totalNum(searchResult.getTotal()).docs(hitList).build()).build();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorContents.QUERY_INDEX_FAILED.getErrno(), ErrorContents.QUERY_INDEX_FAILED.getMsg());
        }
    }

    private String getPutMapping(List<IndexColumn> columns, String type) {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("mappings")
                    .startObject(type).startObject("properties");

            if (!CollectionUtils.isEmpty(columns)) {
                for (IndexColumn indexColumn : columns) {
                    if (!StringUtils.isEmpty(indexColumn.getKey())) {
                        builder.startObject(indexColumn.getKey());
                        if (indexColumn.getType().equals("text")) {
                            builder.field("type", "text").startObject("fields").startObject("keyword")
                                    .field("type", "keyword").field("ignore_above", 256)
                                    .endObject().endObject();
                        } else if (indexColumn.getType().equals("date")) {
                            builder.field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss");
                        } else {
                            builder.field("type", indexColumn.getType());
                        }
                        if (indexColumn.getProperties() != null && !indexColumn.getProperties().isEmpty()) {
                            for (Map.Entry<String, Object> entry : indexColumn.getProperties().entrySet()) {
                                builder.field(entry.getKey(), entry.getValue());
                            }
                        }
                        builder.endObject();
                    }
                }
            }
            builder.endObject().endObject().endObject().endObject();
            log.error("mapping:{}", Strings.toString(builder));
            return Strings.toString(builder);
        } catch (IOException e) {
            throw new BusinessException(ErrorContents.INDEX_MAPPING_FAILD.getErrno(), ErrorContents.INDEX_MAPPING_FAILD.getMsg());
        }

    }

    private String buildQuery(QueryRequest request, QueryCriteria criteria) {
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        if (ArrayUtils.isNotEmpty(criteria.getIncludeFields())) {
            searchSourceBuilder.fetchSource(criteria.getIncludeFields(), null);
        }
        BoolQueryBuilder boolQueryBuilderMust = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryBuilderOr = QueryBuilders.boolQuery();
        if (!CollectionUtils.isEmpty(criteria.getMatches())) {
            criteria.getMatches().forEach(match -> {
                if (!"or".equals(match.getRelative())) {
                    boolQueryBuilderMust.filter(QueryBuilders.matchQuery(match.getKey(), match.getValue()));
                } else {
                    boolQueryBuilderOr.should(QueryBuilders.matchQuery(match.getKey(), match.getValue()));
                }
            });
        }
        if (!CollectionUtils.isEmpty(criteria.getTermList())) {
            criteria.getTermList().forEach(term -> {
                if (!"or".equals(term.getRelative())) {
                    boolQueryBuilderMust.filter(QueryBuilders.termQuery(term.getKey(), term.getValue()));
                } else {
                    boolQueryBuilderOr.should(QueryBuilders.termQuery(term.getKey(), term.getValue()));
                }
            });
        }
        if (!CollectionUtils.isEmpty(criteria.getTermsList())) {
            criteria.getTermsList().forEach(terms -> {
                if (!"or".equals(terms.getRelative())) {
                    boolQueryBuilderMust.filter(QueryBuilders.termsQuery(terms.getKey(), terms.getValues()));
                } else {
                    boolQueryBuilderOr.should(QueryBuilders.termsQuery(terms.getKey(), terms.getValues()));
                }
            });
        }
        if (!CollectionUtils.isEmpty(criteria.getRanges())) {
            criteria.getRanges().forEach(range -> {
                if ("or".equals(range.getRelative())) {
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(range.getKey());
                    if (range.getFrom() != null) {
                        rangeQueryBuilder.gte(range.getFrom());
                    }
                    if (range.getTo() != null) {
                        rangeQueryBuilder.lte(range.getTo());
                    }
                    boolQueryBuilderOr.should(rangeQueryBuilder);
                }else {
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(range.getKey());
                    if (range.getFrom() != null) {
                        rangeQueryBuilder.gte(range.getFrom());
                    }
                    if (range.getTo() != null) {
                        rangeQueryBuilder.lte(range.getTo());
                    }
                    boolQueryBuilderOr.filter(rangeQueryBuilder);
                }
            });
        }
        boolQueryBuilderMust.filter(boolQueryBuilderOr);
        searchSourceBuilder.sort("year", SortOrder.DESC);
        searchSourceBuilder.from(request.getFrom());
        searchSourceBuilder.size(request.getSize());
        return searchSourceBuilder.query(boolQueryBuilderMust).toString();
    }
}
