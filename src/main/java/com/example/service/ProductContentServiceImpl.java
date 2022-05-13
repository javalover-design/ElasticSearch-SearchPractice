package com.example.service;

import com.alibaba.fastjson.JSON;
import com.example.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lambda
 */
@Service
public class ProductContentServiceImpl implements ProductContentService{
    @Autowired
    @Qualifier("getRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    /**
     * 获取数据并将数据存入ES中
     * @param keywords the keywords
     * @return
     * @throws Exception
     */
    @Override
    public boolean parseProductContent(String keywords) throws Exception {
        //获取解析到的内容
        List list = HtmlParseUtil.parseHtml(keywords);
        //将查询到的内容放入ES中
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(new IndexRequest("goods").source(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    /**
     * 此方法用于分页和执行精准搜索
     * @param keyword  the keyword
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return
     * @throws IOException
     */
    @Override
    public List<Map<String, Object>> searchPage(String keyword, Integer pageNo, Integer pageSize) throws IOException {
        if (pageNo<=1){
            pageNo=1;
        }

        //进行基本的条件搜索
        SearchRequest goodsSearch = new SearchRequest("goods");
        //搜索构造
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //执行分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        //精准匹配关键字
        TermQueryBuilder titleQuery = QueryBuilders.termQuery("title", keyword);
        searchSourceBuilder.query(titleQuery);
        //设置超时
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        //不需要设置多个字段高亮
        highlightBuilder.requireFieldMatch(false);
        //设置高亮的有关标签
        highlightBuilder.preTags("<span style='color:red‘>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        //执行搜索
        goodsSearch.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(goodsSearch, RequestOptions.DEFAULT);

        //解析结果
        List<Map<String, Object>> results = new ArrayList<>();

        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //解析高亮的字段
            //获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            if (title!=null){
                //将title的内容取出
                Text[] fragments = title.fragments();
                String defaultTitle="";
                for (Text fragment : fragments) {
                    defaultTitle+=fragment;
                }
                //将高亮显示的标题添加到map中，覆盖原来的标题
                sourceAsMap.put("title",defaultTitle);
            }

            results.add(sourceAsMap);
        }

        return results;
    }


}
