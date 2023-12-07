package com.tzp.excelView.controller;

import com.alibaba.fastjson.JSONObject;
import com.tzp.excelView.util.MsgUtil;
import com.tzp.excelView.util.ReadExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Dong
 */

@RestController
@RequestMapping("/excel")
@Api(value = "ReadExcelController", tags = "读取excel")
public class ReadExcelController {

    @ApiOperation("指定一个文件位置，解析excel")
    @PostMapping("/readExcel")
    public MsgUtil<Object> readExcel(@RequestBody JSONObject json) {
        String filePath = String.valueOf(json.get("filePath"));
        List<List<Object>> excelList = ReadExcelUtil.readExcel(filePath);
        return MsgUtil.success("解析成功", excelList);
    }

}
