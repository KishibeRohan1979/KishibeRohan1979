package com.tzp.excelView.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Dong
 */
@Data
public class ExcelEntity {

    @ApiModelProperty(value = "所有行的内容", name = "rows")
    private List<List<String>> rows;

    @ApiModelProperty(value = "所有列的内容", name = "columns")
    private List<List<String>> columns;

    @ApiModelProperty(value = "所有列的标题", name = "titles")
    private List<String> titles;

    @ApiModelProperty(value = "文件名", name = "fileName")
    private String fileName;

}
