package com.tzp.myWebTest.service.impl;

import com.alibaba.fastjson.JSON;
import com.tzp.myWebTest.dto.EsQueryDTO;
import com.tzp.myWebTest.service.EsDocumentService;
import com.tzp.myWebTest.util.AnalyzerType;
import com.tzp.myWebTest.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EsDocumentServiceImpl<T> implements EsDocumentService<T> {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 同步新增一个文档
     *
     * @param idxName  索引名
     * @param idxId    索引id
     * @param document 文档对象
     */
    @Override
    public IndexResponse createOneDocument(String idxName, String idxId, T document) throws Exception {
        IndexRequest request = new IndexRequest();
        request.index(idxName);
        request.id(idxId);
        request.source(JSON.toJSONString(document, false), XContentType.JSON);
        return restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 同步批量添加文档
     *
     * @param idxName   索引名
     * @param documents 要增加的对象集合
     */
    @Override
    public BulkResponse batchCreate(String idxName, List<T> documents) throws Exception {
        BulkRequest bulkRequest = new BulkRequest();
        // 批量请求处理
        for (T document : documents) {
            bulkRequest.add(
                    // 这里是数据信息
                    new IndexRequest(idxName)
                            .source(JSON.toJSONString(document, false), XContentType.JSON)
            );
        }
        return restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    /**
     * 用JSON字符串创建文档
     *
     * @param idxName     索引名
     * @param idxId       索引id
     * @param jsonContent json字符串
     */
    @Override
    public IndexResponse createByJson(String idxName, String idxId, String jsonContent) throws Exception {
        IndexRequest request = new IndexRequest();
        request.index(idxName);
        request.id(idxId);
        // JSON.toJSONString可能多此一举，后期再优化
        request.source(JSON.toJSONString(jsonContent), XContentType.JSON);
        return restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 真的删除，根据id删除文档
     *
     * @param idxName 索引名
     * @param docId   文档id
     * @return 成功返回true，失败返回false
     */
    @Override
    public Boolean deleteById(String idxName, String docId) {
        DeleteRequest request = new DeleteRequest();
        request.index(idxName);
        request.id(docId);
        try {
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 真的删除，批量删除文档
     *
     * @param idxName 索引名
     * @param docIds  要删除的文档id集合
     */
    @Override
    public BulkResponse batchDeleteByIds(String idxName, List<String> docIds) throws Exception {
        BulkRequest bulkRequest = new BulkRequest();
        // 批量请求处理
        for (String docId : docIds) {
            bulkRequest.add(
                    // 这里是数据信息
                    new DeleteRequest()
                            .index(idxName)
                            .id(docId)
            );
        }
        return restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    /**
     * 根据文档id查找文档
     *
     * @param idxName 索引名
     * @param docId   文档id
     * @param clazz   需要转化的类
     */
    @Override
    public T getById(String idxName, String docId, Class<T> clazz) throws IOException {
        GetRequest request = new GetRequest();
        request.index(idxName);
        request.id(docId);
        GetResponse getResponse = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        // 判断文档是否存在
        if (getResponse.isExists()) {
            String sourceAsString = getResponse.getSourceAsString();
            return JSON.parseObject(sourceAsString, clazz);
        } else {
            return null;
        }
    }

    /**
     * 分页条件查询
     *
     * @param esQueryDTO  查询类
     */
    @Override
    public Map<String, Object> searchByQueryString(EsQueryDTO<T> esQueryDTO) throws IOException {

        Integer pageNo = esQueryDTO.getPageNum();
        Integer pageSize = esQueryDTO.getPageSize();
        // 1.创建查询请求对象
        SearchRequest searchRequest = new SearchRequest(esQueryDTO.getIndexName());
        // 2.构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // (1)查询条件 使用QueryBuilders工具类创建
        // 多字段模糊查询
        String[] fields = {"*"}; // 查询所有字符串类型的字段
        String analyzerType = AnalyzerType.getAnalyzerType(esQueryDTO.getAnalyzerType());
        String queryString = AnalyzerType.deleteNull(esQueryDTO.getQueryString());
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(queryString, fields).analyzer(analyzerType);
        searchSourceBuilder.from(pageNo * pageSize);
        searchSourceBuilder.size(pageSize);
        // (2) 高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("*"); // 高亮所有字段
        highlightBuilder.requireFieldMatch(false); // 多个字段时，匹配不同的字段时需要匹配高亮
        highlightBuilder.preTags("<font color='red'>"); // 高亮前缀
        highlightBuilder.postTags("</font>"); // 高亮后缀
        highlightBuilder.numOfFragments(0);// 不进行限制，完整显示所有高亮内容
        searchSourceBuilder.highlighter(highlightBuilder);
        // (3)条件投入
        searchSourceBuilder.query(queryBuilder);
        // 3.添加条件到请求
        searchRequest.source(searchSourceBuilder);
        // 4.客户端查询请求
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 5.查看返回结果
        List<T> resultList = getResultList(search, esQueryDTO.getQueryClazz());
        return getResultMap(resultList, search, pageSize, pageNo);
    }

    private List<T> getResultList(SearchResponse search, Class<T> clazz) {
        List<T> resultList = new ArrayList<>();
        // 5.查看返回结果
        SearchHits hits = search.getHits();
        SearchHit[] hitsArray = hits.getHits();
        // 文档的循环
        for (SearchHit hit : hitsArray) {
            Map<String, Object> map = hit.getSourceAsMap();
            // 获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            // 高亮字段的循环
            for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
                String fieldName = entry.getKey();
                HighlightField highlight = entry.getValue();
                Text[] fragments = highlight.fragments();
                StringBuilder fragmentString = new StringBuilder();
                // 字段（例：id、name什么）的循环
                for (Text fragment : fragments) {
                    fragmentString.append(fragment);
                }
                // 替换原有字段值
                map.put(fieldName, fragmentString.toString());
            }
            resultList.add(JSON.parseObject(JSON.toJSONString(map), clazz));
        }
        return resultList;
    }

    /**
     * 分页查询
     *
     * @param esQueryDTO 查询类
     */
    @Override
    public Map<String, Object> searchByPage(EsQueryDTO<T> esQueryDTO) throws IOException{
        Integer pageNo = esQueryDTO.getPageNum();
        Integer pageSize = esQueryDTO.getPageSize();
        List<T> resultList = new ArrayList<>();
        // 1.创建查询请求对象
        SearchRequest searchRequest = new SearchRequest(esQueryDTO.getIndexName());

        // 2.构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(pageNo * pageSize);
        searchSourceBuilder.size(pageSize);

        // 3.添加条件到请求
        searchRequest.source(searchSourceBuilder);

        // 4.客户端查询请求
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 5.查看返回结果
        SearchHits hits = search.getHits();
        SearchHit[] hitsArray = hits.getHits();
        for (SearchHit hit : hitsArray) {
            Map<String, Object> map = hit.getSourceAsMap();
            resultList.add(JSON.parseObject(JSON.toJSONString(map), esQueryDTO.getQueryClazz()));
        }
        return getResultMap(resultList, search, pageSize, pageNo);
    }

    /**
     * 根据对象查询
     *
     * @param esQueryDTO 查询类
     */
    @Override
    public Map<String, Object> searchByQueryObject(EsQueryDTO<T> esQueryDTO) throws Exception {
        Integer pageNo = esQueryDTO.getPageNum();
        Integer pageSize = esQueryDTO.getPageSize();
        // 1.创建查询请求对象
        SearchRequest searchRequest = new SearchRequest(esQueryDTO.getIndexName());
        // 2.构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // (1)查询条件 使用QueryBuilders工具类创建
        // 多字段模糊查询
        String[] fields = {"*"}; // 查询所有字符串类型的字段
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 多字段模糊查询
        String analyzerType = AnalyzerType.getAnalyzerType(esQueryDTO.getAnalyzerType());
        if (!StringUtils.isBlank(esQueryDTO.getQueryString())) {
            String queryString = AnalyzerType.deleteNull(esQueryDTO.getQueryString());
            // 设置查询条件
            MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(queryString, fields)
                    .analyzer(analyzerType);
            searchSourceBuilder.query(boolQueryBuilder.must(queryBuilder));
        }
        // 精确查询
        if (esQueryDTO.getQueryObject() != null) {
            // (2) 精确匹配和范围查询过滤条件
            Map<String, Object> javaMap = new HashMap<>();
            // 利用Java反射获取泛型t的属性
            Class<?> tClass = esQueryDTO.getQueryObject().getClass();
            Field[] javaFields = tClass.getDeclaredFields();
            for (Field field : javaFields) {
                String attributeName = field.getName();
                Class<?> attributeType = field.getType();
                System.out.println("属性名字：" + attributeName);
                System.out.println("属性类型：" + attributeType);
                // 转化type为自定义对象
//            Object object = type.getDeclaredConstructor().newInstance();
                // 获取get...（get属性）方法
                Method getNameMethod = tClass.getMethod("get"+ capitalizeFirstLetter(attributeName));
                // 调用get...（get属性）方法
                Object nameValue = getNameMethod.invoke(esQueryDTO.getQueryObject());
                System.out.println("这个字段是空值？" + (null == nameValue));
                if ( null != nameValue ) {
                    javaMap.put(attributeName, nameValue);
                }
                System.out.println(attributeName + "：" + nameValue);
                System.out.println("============================================");
            }
            // 精确匹配字段
            for (Map.Entry<String, Object> entry : javaMap.entrySet()) {
                TermQueryBuilder termQuery = QueryBuilders.termQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.filter(termQuery);
            }
        }
        // 排序查询
        if (!StringUtils.isBlank(esQueryDTO.getOrderField())) {
            SortBuilder<FieldSortBuilder> sortBuilder = SortBuilders.fieldSort(esQueryDTO.getOrderField());
            if ( "asc".equals(esQueryDTO.getOrderType()) ) {
                sortBuilder.order(SortOrder.ASC);
            } else {
                sortBuilder.order(SortOrder.DESC);
            }
            searchSourceBuilder.sort(sortBuilder);
        }
        // 范围查询
        if (!StringUtils.isBlank(esQueryDTO.getRangeField())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(esQueryDTO.getRangeField()).gte(esQueryDTO.getStartValue()).lt(esQueryDTO.getEndValue());
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        // 模糊查询字段
        if (esQueryDTO.getMatchMap() != null) {
            for (Map.Entry<String, Object> entry : esQueryDTO.getMatchMap().entrySet()) {
                MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(entry.getValue().toString(), entry.getKey())
                        .analyzer(analyzerType);
                boolQueryBuilder.filter(queryBuilder);
            }
        }
        // 精确查询字段
        if (esQueryDTO.getTermMap() != null) {
            for (Map.Entry<String, Object> entry : esQueryDTO.getTermMap().entrySet()) {
                TermQueryBuilder termQuery = QueryBuilders.termQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.filter(termQuery);
            }
        }
        searchSourceBuilder.query(boolQueryBuilder);
        // (3) 高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("*"); // 高亮所有字段
        highlightBuilder.requireFieldMatch(false); // 多个字段时，匹配不同的字段时需要匹配高亮
        highlightBuilder.preTags("<font color='red'>"); // 高亮前缀
        highlightBuilder.postTags("</font>"); // 高亮后缀
        highlightBuilder.numOfFragments(0);// 不进行限制，完整显示所有高亮内容
        searchSourceBuilder.highlighter(highlightBuilder);
        // 4.添加条件到请求
        searchSourceBuilder.from(pageNo * pageSize);
        searchSourceBuilder.size(pageSize);
        searchRequest.source(searchSourceBuilder);
        // 5.客户端
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 5.查看返回结果
        List<T> resultList = getResultList(search, esQueryDTO.getQueryClazz());
        return getResultMap(resultList, search, pageSize, pageNo);
    }

    /**
     * 首字母大写
     * @param str 需要转化的英语字符串
     * @return 首字母大写的字符串
     */
    private static String capitalizeFirstLetter(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static <T> Map<String, Object> getResultMap(List<T> resultList, SearchResponse search, Integer pageSize, Integer pageNo) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", resultList);
        resultMap.put("page", PageUtil.getPage((int) search.getHits().getTotalHits().value, pageSize, pageNo));
        return resultMap;
    }

    @Override
    public boolean updateById(String indexName, T t, String id, Class<T> clazz) {

        return false;
    }


    /**
     *
     * 异步新增文档
     *
     * @param idxName  索引名
     * @param idxId    索引id
     * @param document 文档对象
     */
//    @Override
//    public void createAsync(String idxName, String idxId, T document) {
//        elasticsearchAsyncClient.index(idx -> idx
//            .index(idxName)
//            .id(idxId)
//            .document(document)
////        ).thenApply(
//            // 同步，这个上述的创建完成之后，再干什么
//        ).whenComplete(
//            (response, error) -> {
//                if ( response != null ) {
//                    // 回调方法，一般用来处理返回值
//                    System.out.println(response.forcedRefresh());
//                } else {
//                    error.printStackTrace();
//                }
//            }
//        );
//    }

}