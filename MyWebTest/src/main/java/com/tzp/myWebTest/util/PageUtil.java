package com.tzp.myWebTest.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageUtil implements Serializable {

    private Integer total;

    private Integer size;

    private Integer totalPage;

    private Integer currentPage;

    public PageUtil(Integer total, Integer size, Integer totalPage, Integer currentPage) {
        this.total = total;
        this.size = size;
        this.totalPage = totalPage;
        this.currentPage = currentPage;
    }

    public PageUtil() {
    }

    public static PageUtil getPage(Integer total, Integer size, Integer currentPage) {
        int totalPage;
        if (total % size == 0) {
            totalPage = total / size;
        } else {
            totalPage = total / size + 1;
        }
        return new PageUtil(total, size, totalPage, currentPage);
    }

}
