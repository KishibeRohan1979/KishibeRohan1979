package com.tzp.myWebTest.service;


import com.tzp.myWebTest.dto.EsQueryDTO;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     * @param esQueryDTO 查询类
     * @return 查询结果
     */
    Map<String, Object> searchByQueryString(EsQueryDTO<T> esQueryDTO) throws IOException;

    /**
     * 分页查询
     *
     * @param esQueryDTO 查询类
     */
    Map<String, Object> searchByPage(EsQueryDTO<T> esQueryDTO) throws IOException;

    /**
     * 分页对象精确查询，字符模糊查询，某个字段某个值的
     *
     * @param  esQueryDTO 查询类
     */
    Map<String, Object> searchByQueryObject(EsQueryDTO<T> esQueryDTO) throws Exception;

    /**
     * 分页对象精确查询，字符模糊查询，某个字段某个值的
     *
     * @param esQueryDTO 查询类
     * @return list
     */
//    List<T> searchByQueryObjectMatchAndTerm(EsQueryDTO<T> esQueryDTO) throws Exception;

    /**
     * 修改索引的数据
     *
     * @param indexName  索引名
     * @param clazz    clazz  封装的实现
     */
    boolean updateById(String indexName, T t, String id, Class<T> clazz);

}