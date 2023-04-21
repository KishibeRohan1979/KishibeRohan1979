package com.tzp.myWebTest.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.CreateOperation;
import co.elastic.clients.elasticsearch.core.bulk.DeleteOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.tzp.myWebTest.service.EsDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EsDocumentServiceImpl<T> implements EsDocumentService<T> {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private ElasticsearchAsyncClient elasticsearchAsyncClient;

    /**
     * 同步新增一个文档
     *
     * @param idxName  索引名
     * @param idxId    索引id
     * @param document 文档对象
     */
    @Override
    public IndexResponse createByFluentDSL(String idxName, String idxId, T document) throws Exception {
        IndexRequest.Builder<T> indexReqBuilder = new IndexRequest.Builder<>();
        indexReqBuilder.index(idxName);
        indexReqBuilder.id(idxId);
        indexReqBuilder.document(document);
        return elasticsearchClient.index(indexReqBuilder.build());
    }

    /**
     * 同步批量添加文档
     *
     * @param idxName   索引名
     * @param documents 要增加的对象集合
     */
    @Override
    public BulkResponse batchCreate(String idxName, List<T> documents) throws Exception {
        List<BulkOperation> operations = new ArrayList<>();
        // TODO 可以将 Object定义为一个文档基类。比如 ESDocument类
        // 将每一个product对象都放入builder中
        for (T document : documents) {
            CreateOperation<T> operation = new CreateOperation.Builder<T>()
                    .index(idxName)
                    .document(document)
                    .build();
            BulkOperation bulk = new BulkOperation.Builder().create(operation).build();
            operations.add(bulk);
        }
        BulkRequest.Builder builder = new BulkRequest.Builder();
        builder.operations(operations);
        return elasticsearchClient.bulk(builder.build());
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
        return elasticsearchClient.index(i -> i
                .index(idxName)
                .id(idxId)
                .withJson(new StringReader(jsonContent))
        );
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
        DeleteRequest request = new DeleteRequest.Builder()
                .index(idxName)
                .id(docId)
                .build();
        try {
            elasticsearchClient.delete(request);
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
        List<BulkOperation> operations = new ArrayList<>();
        // TODO 可以将 Object定义为一个文档基类。比如 ESDocument类
        // 将每一个product对象都放入builder中
        for (String id : docIds) {
            DeleteOperation operation = new DeleteOperation.Builder()
                    .index(idxName)
                    .id(id)
                    .build();
            BulkOperation bulk = new BulkOperation.Builder().delete(operation).build();
            operations.add(bulk);
        }
        BulkRequest.Builder builder = new BulkRequest.Builder();
        builder.operations(operations);
        return elasticsearchClient.bulk(builder.build());
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
        GetResponse<T> response = elasticsearchClient.get(g -> g
                        .index(idxName)
                        .id(docId),
                clazz);
        return response.found() ? response.source() : null;
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
    public List<T> searchByQueryString(String idxName, String queryString, Integer pageNo, Integer pageSize, Class<T> clazz) {

        // 防止* ？等通配符被转译
        queryString = queryString.replaceAll("\\*", "\\\\*");
        queryString = queryString.replaceAll("\\?", "\\\\?");

        // 1.构建查询的对象
        QueryStringQuery stringQuery = new QueryStringQuery.Builder()
                //.fields("name", "description")
                // * 是通配符，代表模糊查询。但是避免使用一个以通配符开头的模式
                .query("*" + queryString + "*")
                .build();

        Query query = new Query.Builder()
                .queryString(stringQuery)
                .build();

        // 2.搜索
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(idxName)
                .from(pageNo * pageSize)
                .size(pageSize)
                .query(query)
                .build();

        try {
            return this.elasticsearchClient.search(searchRequest, clazz)
                    .hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
    public List<T> searchByPage(String idxName, Integer pageNo, Integer pageSize, Class<T> clazz) {
        // 1.搜索
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder();
        searchBuilder
            .index(idxName)
            .from(pageNo * pageSize)
            .size(pageSize);
        SearchRequest searchRequest = searchBuilder.build();
        try {
            return this.elasticsearchClient.search(searchRequest, clazz)
                    .hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     *
     * @param idxName  索引名
     * @param t        对象
     * @param pageNo   当前页
     * @param pageSize 每页多少条数据
     * @param clazz    clazz  封装的实现
     */
    @Override
    public List<T> searchByQueryObject(String idxName, T t, Integer pageNo, Integer pageSize, Class<T> clazz) {

        return null;
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
    @Override
    public void createAsync(String idxName, String idxId, T document) {
        elasticsearchAsyncClient.index(idx -> idx
            .index(idxName)
            .id(idxId)
            .document(document)
//        ).thenApply(
            // 同步，这个上述的创建完成之后，再干什么
        ).whenComplete(
            (response, error) -> {
                if ( response != null ) {
                    // 回调方法，一般用来处理返回值
                    System.out.println(response.forcedRefresh());
                } else {
                    error.printStackTrace();
                }
            }
        );
    }
}