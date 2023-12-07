package com.tzp.excelView;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Dong
 */
@SpringBootApplication
public class ExcelViewApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcelViewApplication.class, args);
        System.out.println("http://localhost:9990/doc.html");
    }

}
