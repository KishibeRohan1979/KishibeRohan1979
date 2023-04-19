package com.tzp.myWebTest.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Setter;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

// 配置的前缀
@ConfigurationProperties(prefix = "elasticsearch")
@Configuration
public class ESClientConfig {

    /**
     * 多个IP逗号隔开
     */
    @Setter
    private String hosts;

    /**
     * 同步方式
     *
     * @return
     */
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        HttpHost[] httpHosts = toHttpHost();
        // Create the RestClient
        RestClient restClient = RestClient.builder(httpHosts).build();
        // Create the transport with a Jackson mapper
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        // create the API client
        return new ElasticsearchClient(transport);
    }

    /**
     * 异步方式
     *
     * @return
     */
    @Bean
    public ElasticsearchAsyncClient elasticsearchAsyncClient() {
        HttpHost[] httpHosts = toHttpHost();
        RestClient restClient = RestClient.builder(httpHosts).build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchAsyncClient(transport);
    }

    /**
     * 解析配置的字符串hosts，转为HttpHost对象数组
     *
     * @return
     */
    private HttpHost[] toHttpHost() {
        if (!StringUtils.hasLength(hosts)) {
            throw new RuntimeException("无效的elasticsearch配置。elasticsearch.hosts不能为空！");
        }

        // 多个IP逗号隔开
        String[] hostArray = hosts.split(",");
        HttpHost[] httpHosts = new HttpHost[hostArray.length];
        HttpHost httpHost;
        for (int i = 0; i < hostArray.length; i++) {
            String[] strings = hostArray[i].split(":");
            httpHost = new HttpHost(strings[0], Integer.parseInt(strings[1]), "http");
            httpHosts[i] = httpHost;
        }

        return httpHosts;
    }

}

