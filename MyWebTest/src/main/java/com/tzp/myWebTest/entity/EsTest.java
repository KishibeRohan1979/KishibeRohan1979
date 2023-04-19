package com.tzp.myWebTest.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EsTest implements Serializable {

    @ApiModelProperty(value = "id", name = "id")
    private String id;

    @ApiModelProperty(value = "姓名", name = "name")
    private String name;

    @ApiModelProperty(value = "性别", name = "sex")
    private String sex;

    @ApiModelProperty(value = "年龄", name = "age")
    private Integer age;

}
