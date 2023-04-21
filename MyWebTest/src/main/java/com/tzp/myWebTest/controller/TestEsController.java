package com.tzp.myWebTest.controller;

import com.tzp.myWebTest.aop.EnableAsync;
import com.tzp.myWebTest.dto.EsQueryDTO;
import com.tzp.myWebTest.entity.EsTest;
import com.tzp.myWebTest.service.AsyncService;
import com.tzp.myWebTest.service.EsDocumentService;
import com.tzp.myWebTest.service.TestService;
import com.tzp.myWebTest.util.AsyncMsgUtil;
import com.tzp.myWebTest.util.MsgUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/esTest")
@Api(value = "TestEsController", tags = "ES测试")
public class TestEsController {

    // es_test数据索引名
    public static final String ES_TEST_DATA = "es_test_data";

    @Autowired
    private EsDocumentService<EsTest> esTestDocumentService;

    @Autowired
    private TestService testService;

    @Autowired
    private AsyncService asyncService;

    @ApiOperation("添加一个新文档")
    @PostMapping("/addNewDocument")
    public MsgUtil addNewDocument(@RequestBody EsTest es) {
        try {
            esTestDocumentService.createByFluentDSL(ES_TEST_DATA, null, es);
            return MsgUtil.success("添加成功", es);
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("添加失败", e.getMessage());
        }
    }

    @ApiOperation("批量添加新文档")
    @PostMapping("/addNewDocumentByBath")
    public MsgUtil addNewDocumentByBath(@RequestBody List<EsTest> list) {
        try {
            esTestDocumentService.batchCreate(ES_TEST_DATA, list);
            return MsgUtil.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("添加失败", e.getMessage());
        }
    }

    @ApiOperation("通过json字符串的方式添加文档")
    @PostMapping("/addNewDocumentByJson")
    public MsgUtil addNewDocumentByJson(@RequestBody String jsonString) {
        try {
            esTestDocumentService.createByJson(ES_TEST_DATA, null, jsonString);
            return MsgUtil.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("添加失败", e.getMessage());
        }
    }

    @ApiOperation("查询文档")
    @PostMapping("/getDocument")
    public MsgUtil getDocument(@RequestBody EsQueryDTO dto) {
        try {
            List<EsTest> esTests;
            if (StringUtils.isBlank(dto.getQueryString())) {
                esTests = esTestDocumentService.searchByPage(ES_TEST_DATA, dto.getPageNum(), dto.getPageSize(), EsTest.class);
            } else {
                esTests = esTestDocumentService.searchByQueryString(ES_TEST_DATA, dto.getQueryString(), dto.getPageNum(), dto.getPageSize(), EsTest.class);
            }
            return MsgUtil.success("查询成功", esTests);
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("查询失败", e.getMessage());
        }
    }

    @EnableAsync
    @ApiOperation("多线程添加")
    @PostMapping("/addNewDocumentByBathAndAsync")
    public MsgUtil addNewDocumentByBathAndAsync(@RequestBody List<EsTest> list) {
        try {
            testService.demo(list);
            return MsgUtil.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("添加失败", e.getMessage());
        }
    }

    @ApiOperation("查询异步接口消息")
    @PostMapping("/getAsyncMsg")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "当时返回的检索id", required = true, dataType = "string", paramType = "query", defaultValue = "")
    })
    public MsgUtil getAsyncMsg(String id) {
        AsyncMsgUtil asyncMsg = asyncService.findAsyncMsgUtil(id);
        return MsgUtil.success("请求成功", asyncMsg);
    }

}
