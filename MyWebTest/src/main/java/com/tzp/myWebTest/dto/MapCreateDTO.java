package com.tzp.myWebTest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MapCreateDTO {

    @ApiModelProperty(value = "查询的类型", name = "type", dataType="String", notes="查询的类型", required=true)
    private String type;

    @ApiModelProperty(value = "视频或者评论的id", name = "oid", dataType="String", notes="视频或者评论的id", required=true)
    private String oid;

}
