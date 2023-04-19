package com.tzp.myWebTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebToolApplication.class, args);
        System.out.println("http://localhost:9990/doc.html");
    }

}
