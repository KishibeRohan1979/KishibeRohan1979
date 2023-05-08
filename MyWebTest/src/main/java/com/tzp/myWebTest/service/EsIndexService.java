package com.tzp.myWebTest.service;

import java.io.IOException;

public interface EsIndexService {

    /**
     * 新建索引，指定索引名称，指定创建json
     *
     * @param indexName 索引名
     * @param jsonString json字符串
     * @return boolean
     * @throws IOException
     */
    boolean createIndex(String indexName, String jsonString) throws Exception;

    /**
     * 新建索引，指定索引名称，只需要提供索引名即可，使用默认的json
     *
     * @param indexName 索引名
     * @return boolean
     * @throws IOException
     */
    boolean createIndex(String indexName) throws Exception;

    /**
     * 删除索引
     *
     * @param indexName 索引名
     * @return boolean
     * @throws IOException
     */
    boolean deleteIndex(String indexName) throws Exception;


    /**
     * 检查指定名称的索引是否存在
     *
     * @param indexName 索引名
     * @return - true：存在
     * @throws IOException
     */
    boolean indexExists(String indexName) throws Exception;



}
