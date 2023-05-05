package com.tzp.myWebTest.service;


import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;

import java.io.IOException;
import java.util.List;

public interface EsDocumentService<T> {

    // 同步方法

    /**
     * 新增一个文档
     *
     * @param idxName  索引名
     * @param idxId    索引id
     * @param document 文档对象
     */
    IndexResponse createOneDocument(String idxName, String idxId, T document) throws Exception;

    /**
     * 批量增加文档
     *
     * @param idxName   索引名
     * @param documents 要增加的对象集合
     * @return 批量操作的结果
     */
    BulkResponse batchCreate(String idxName, List<T> documents) throws Exception;

    /**
     * 用JSON字符串创建文档
     *
     * @param idxName     索引名
     * @param idxId       索引id
     * @param jsonContent json字符串
     */
    IndexResponse createByJson(String idxName, String idxId, String jsonContent) throws Exception;

    /**
     * 根据文档id删除文档
     *
     * @param idxName 索引名
     * @param docId   文档id
     * @return 删除结果
     */
    Boolean deleteById(String idxName, String docId);

    /**
     * 批量删除文档
     *
     * @param idxName 索引名
     * @param docIds  要删除的文档id集合
     */
    BulkResponse batchDeleteByIds(String idxName, List<String> docIds) throws Exception;

    /**
     * 根据文档id查找文档
     *
     * @param idxName 索引名
     * @param docId   文档id
     * @return 查询结果
     */
    T getById(String idxName, String docId, Class<T> clazz) throws IOException;

    /**
     * 分页条件查询
     *
     * @param idxName  索引名
     * @param pageNo   当前页
     * @param pageSize 每页多少条数据
     * @param clazz    clazz  封装的实现
     * @return 查询结果
     */
    List<T> searchByQueryString(String idxName, String queryString, Integer pageNo, Integer pageSize, Class<T> clazz, String analyzerType) throws IOException;

    /**
     * 分页查询
     *
     * @param idxName  索引名
     * @param pageNo   当前页
     * @param pageSize 每页多少条数据
     * @param clazz    clazz  封装的实现
     */
    List<T> searchByPage(String idxName, Integer pageNo, Integer pageSize, Class<T> clazz) throws IOException;

    /**
     * 精确查询，某个字段某个值的
     *
     * @param idxName  索引名
     * @param pageNo   当前页
     * @param t        对象
     * @param pageSize 每页多少条数据
     * @param clazz    clazz  封装的实现
     */
    List<T> searchByQueryObject(String idxName, T t, String queryString, Integer pageNo, Integer pageSize, Class<T> clazz, String analyzerType) throws Exception;

    /**
     * 修改索引的数据
     *
     * @param indexName  索引名
     * @param clazz    clazz  封装的实现
     */
    boolean updateById(String indexName, T t, String id, Class clazz);

}