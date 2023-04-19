package com.tzp.myTestDemo.esTest;

import co.elastic.clients.elasticsearch.*;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.*;
import org.apache.http.client.*;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.*;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.*;
import org.elasticsearch.client.*;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.file.*;
import java.security.KeyStore;
import java.security.cert.*;

public class ESClient {

    public static void main(String[] args) throws Exception {
        initESConnectionByHttp();
    }

    // 初始化ES服务器的连接（http方式）
    private static void initESConnectionByHttp() throws Exception {
        // Create the low-level client 创建低级客户端
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        // Create the transport with a Jackson mapper 使用Jackson映射器创建传输层
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client 创建API客户端
        ElasticsearchClient client = new ElasticsearchClient(transport);
        //关闭连接
        transport.close();
        restClient.close();
    }

    // 初始化ES服务器的连接（使用https的方式，需要有证书）
    private static void initESConnectionByHttps() throws Exception {
        // 获取客户端对象
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // 配置用户名，密码
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("", ""));
        Path caCertificatePath = Paths.get("ca.crt");
        CertificateFactory factory =
                CertificateFactory.getInstance("X.509");
        Certificate trustedCa;
        try (InputStream is = Files.newInputStream(caCertificatePath)) {
            trustedCa = factory.generateCertificate(is);
        }
        KeyStore trustStore = KeyStore.getInstance("pkcs12");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca", trustedCa);
        SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                .loadTrustMaterial(trustStore, null);
        final SSLContext sslContext = sslContextBuilder.build();
        RestClientBuilder builder = RestClient.builder(
                        new HttpHost("127.0.0.1", 9200, "http"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setSSLContext(sslContext)
                                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        RestClient restClient = builder.build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);
        ElasticsearchAsyncClient asyncClient = new ElasticsearchAsyncClient(transport);
        transport.close();

    }

}
