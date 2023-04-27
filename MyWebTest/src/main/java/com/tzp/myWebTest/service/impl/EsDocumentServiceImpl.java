package com.tzp.myWebTest.service.impl;

import com.alibaba.fastjson.JSON;
import com.tzp.myWebTest.service.EsDocumentService;
import com.tzp.myWebTest.util.AnalyzerType;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
     * @param idxName  索引名
     * @param queryString 查询关键字
     * @param pageNo   当前页
     * @param pageSize 每页多少条数据
     * @param clazz    clazz  封装的实现
     */
    @Override
    public List<T> searchByQueryString(String idxName, String queryString, Integer pageNo, Integer pageSize, Class<T> clazz, String analyzerType) throws IOException {

        List<T> resultList = new ArrayList<>();
        // 1.创建查询请求对象
        SearchRequest searchRequest = new SearchRequest();
        // 2.构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // (1)查询条件 使用QueryBuilders工具类创建
        // 多字段模糊查询
        String[] fields = {"*"}; // 查询所有字符串类型的字段
        analyzerType = AnalyzerType.getAnalyzerType(analyzerType);
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
        System.out.println(searchRequest);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 5.查看返回结果
        SearchHits hits = search.getHits();
        SearchHit[] hitsArray = hits.getHits();
        for (SearchHit hit : hitsArray) {
            Map<String, Object> map = hit.getSourceAsMap();
            System.out.println(map);
            System.out.println("================================================");
            // 获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
                String fieldName = entry.getKey();
                System.out.println("fieldName:==" + fieldName);
                System.out.println("================================================");
                HighlightField highlight = entry.getValue();
                System.out.println("highlight:==" + highlight);
                System.out.println("================================================");
                Text[] fragments = highlight.fragments();
                System.out.println("fragments:==" + fragments);
                StringBuilder fragmentString = new StringBuilder();
                for (Text fragment : fragments) {
                    fragmentString.append(fragment);
                }
                // 替换原有字段值
                String[] point = (fieldName+".").split("\\.");
                String beforePoint = point[0];
                map.put(beforePoint, fragmentString.toString());
            }
            resultList.add(JSON.parseObject(JSON.toJSONString(map), clazz));
        }
        return resultList;

    }

    /**
     * 分页查询
     *
     * @param idxName  索引名
     * @param pageNo   当前页
     * @param pageSize 每页多少条数据
     * @param clazz    clazz  封装的实现
     */
    @Override
    public List<T> searchByPage(String idxName, Integer pageNo, Integer pageSize, Class<T> clazz) throws IOException{
        List<T> resultList = new ArrayList<>();
        // 1.创建查询请求对象
        SearchRequest searchRequest = new SearchRequest();

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
            resultList.add(JSON.parseObject(JSON.toJSONString(map), clazz));
        }
        return resultList;
    }

    /**
     * 根据对象查询
     *
     * @param idxName  索引名
     * @param t        对象
     * @param pageNo   当前页
     * @param pageSize 每页多少条数据
     * @param clazz    clazz  封装的实现
     */
    @Override
    public List<T> searchByQueryObject(String idxName, T t, Integer pageNo, Integer pageSize, Class<T> clazz) throws Exception {
        // 利用Java反射获取泛型t的属性
        Class<?> tClass = t.getClass();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            String attributeName = field.getName();
            Class<?> attributeType = field.getType();
            System.out.println("属性名字：" + attributeName);
            System.out.println("属性类型：" + attributeType);
            // 转化type为自定义对象
//            Object object = type.getDeclaredConstructor().newInstance();
            // 获取get...（get属性）方法
            Method getNameMethod = t.getClass().getMethod("get"+ capitalizeFirstLetter(attributeName));
            // 调用get...（get属性）方法
            Object nameValue = getNameMethod.invoke(t);
            System.out.println(null == nameValue);
            System.out.println(nameValue);
        }

        return null;
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

    @Override
    public boolean updateById(String indexName, T t, String id, Class clazz) {

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