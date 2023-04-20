package com.tzp.myWebTest.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AsyncMsgUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public AsyncMsgUtil() {
        this.status = "1";
    }

    private String id;

    @ApiModelProperty(value = "状态 1运行中 0结束 -1异常")
    private String status;

    private Object result;

    @ApiModelProperty(value = "任务进度")
    private String progress;
}