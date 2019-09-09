package com.movie.search.moviesearch.service.movie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.movie.search.moviesearch.api.index.IndexService;
import com.movie.search.moviesearch.api.movie.MovieService;
import com.movie.search.moviesearch.exceptionhandler.BusinessException;
import com.movie.search.moviesearch.exceptionhandler.ErrorContents;
import com.movie.search.moviesearch.request.*;
import com.movie.search.moviesearch.request.movie.MovieQueryRequest;
import com.movie.search.moviesearch.request.movie.MovieUpdateRequest;
import com.movie.search.moviesearch.response.CommonResponse;
import com.movie.search.moviesearch.response.QueryResponse;
import com.movie.search.moviesearch.response.UpdateResponse;
import com.movie.search.moviesearch.response.movie.MovieDoc;
import com.movie.search.moviesearch.response.movie.MovieQueryResponse;
import com.movie.search.moviesearch.util.data.DataTrans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private IndexService indexService;

    @Value("${index.movie.name}")
    private String indexName;

    @Value("${index.movie.type}")
    private String indexType;

    @Override
    public CommonResponse<MovieQueryResponse> queryMovie(MovieQueryRequest request) {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setIndexName(indexName);
        queryRequest.setIndexType(indexType);
        queryRequest.setFrom((request.getPageNum()-1)*request.getPageSize());
        queryRequest.setSize(request.getPageSize());
        QueryCriteria criteria = new QueryCriteria();
        List<Term> termList = new ArrayList<>();
        List<Terms> termsList = new ArrayList<>();
        List<Match> matches = new ArrayList<>();
        List<Range> rangeList = new ArrayList<>();
        //年份
        if (!StringUtils.isEmpty(request.getYears())) {
            String[] yesrs = request.getYears().split("-");
            if (yesrs.length <= 1 && !yesrs[0].equals("全部")) {
                if (yesrs[0].contains("以前")) {
                    Range range = new Range();
                    range.setKey("year");
                    range.setRelative("and");
                    range.setTo(Integer.valueOf(yesrs[0].split("以前")[0]));
                    rangeList.add(range);
                }else {
                    Term term = new Term();
                    term.setKey("year");
                    term.setValue(yesrs[0]);
                    term.setRelative("and");
                    termList.add(term);
                } }else {
                Range range = new Range();
                range.setFrom(Integer.valueOf(yesrs[0]));
                range.setTo(Integer.valueOf(yesrs[1]));
                range.setKey("year");
                range.setRelative("and");
                rangeList.add(range);
            }
        }
        //片源地
        if (!StringUtils.isEmpty(request.getCountries()) && !request.getCountries().equals("全部")) {
            Match term = new Match();
            term.setKey("country");
            term.setValue(request.getCountries());
            term.setRelative("and");
            matches.add(term);
        }
        //类别
        if (!StringUtils.isEmpty(request.getCategory()) && !request.getCategory().equals("全部")) {
            Match match = new Match();
            match.setKey("category");
            match.setValue(request.getCategory());
            match.setRelative("and");
            matches.add(match);
        }
        //评分
        if (request.getComment() != null) {
            Range range = new Range();
            range.setKey("comment");
            range.setFrom(request.getComment());
            range.setTo(10.0);
            range.setRelative("and");
            rangeList.add(range);
        }
        //片名
        if (!StringUtils.isEmpty(request.getName())) {
            Match match = new Match();
            match.setKey("name");
            match.setValue(request.getName());
            match.setRelative("or");
            matches.add(match);
            match = new Match();
            match.setKey("subName");
            match.setValue(request.getName());
            match.setRelative("or");
            matches.add(match);
            match = new Match();
            match.setKey("title");
            match.setValue(request.getName());
            match.setRelative("or");
            matches.add(match);

        }
        //语言
        if (!StringUtils.isEmpty(request.getLanguage()) && !request.getLanguage().equals("全部")) {
            Match match = new Match();
            match.setKey("language");
            match.setValue(request.getLanguage());
            match.setRelative("and");
            matches.add(match);
        }
        criteria.setMatches(matches);
        criteria.setTermsList(termsList);
        criteria.setTermList(termList);
        criteria.setRanges(rangeList);
        queryRequest.setCriteria(criteria);
        CommonResponse<QueryResponse<JSONObject>> docResp = indexService.queryDocs(queryRequest);
        List<MovieDoc> movieDocs = new ArrayList<>();
        docResp.getData().getDocs().forEach(doc -> {
            MovieDoc movieDoc = null;
            try{
                movieDoc =  DataTrans.jsonObjectToBean(doc, MovieDoc.class);
            }catch (Exception e) {
                e.printStackTrace();
            }
            if (movieDoc != null) {
                movieDocs.add(movieDoc);
            }
        });
        return CommonResponse.<MovieQueryResponse>builder().errno(0L).msg("SUCCESS").data(MovieQueryResponse.builder()
                .size(docResp.getData().getSize()).totalNum(docResp.getData().getTotalNum()).docs(movieDocs).build()).build();
    }

    @Override
    public CommonResponse<UpdateResponse> updateMovieDocs(MovieUpdateRequest request) {
        if (CollectionUtils.isEmpty(request.getMovieDocs())) {
            throw new BusinessException(ErrorContents.EMPTY_DOC.getErrno(), ErrorContents.EMPTY_DOC.getMsg());
        }
        UpdateDocRequest updateDocRequest = new UpdateDocRequest();
        updateDocRequest.setIndexType(indexType);
        updateDocRequest.setIndexName(indexName);
        List<DocParam> docParamList = new ArrayList<>();
        request.getMovieDocs().forEach(movieParam -> {
            DocParam docParam = new DocParam();
            docParam.setIndexId(movieParam.getName());
            docParam.setDoc(DataTrans.beanToJsonObject(movieParam));
            docParamList.add(docParam);
        });
        updateDocRequest.setDocs(docParamList);
        return indexService.updateOrInsertDocs(updateDocRequest);
    }
}
