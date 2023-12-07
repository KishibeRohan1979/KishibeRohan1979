package com.tzp.excelView.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig {

    @Bean
    public Docket docket() {
        // 添加接口请求头参数配置 没有的话 可以忽略
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //是否启动swagger 默认启动
                .enable(true)
                //所在分组
                .groupName("tzp")
                .select()
                //指定扫描的包路径
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("太宰皮の网页小工具系统",
                "服务接口部分",
                "v1.0",
                "http://localhost/",
                //作者信息
                new Contact("太宰皮", "", ""),
                "Apache 2.0",
                "",
                new ArrayList());
    }

}
