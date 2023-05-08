package com.tzp.myWebTest.controller;

import com.tzp.myWebTest.aop.EnableAsync;
import com.tzp.myWebTest.dto.EsQueryDTO;
import com.tzp.myWebTest.entity.EsTest;
import com.tzp.myWebTest.service.AsyncService;
import com.tzp.myWebTest.service.EsDocumentService;
import com.tzp.myWebTest.service.EsIndexService;
import com.tzp.myWebTest.service.TestService;
import com.tzp.myWebTest.util.AsyncMsgUtil;
import com.tzp.myWebTest.util.MsgUtil;
import com.tzp.myWebTest.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private EsIndexService esIndexService;

    @ApiOperation("添加一个新文档")
    @PostMapping("/addNewDocument")
    public MsgUtil<Object> addNewDocument(@RequestBody EsTest es) {
        try {
            esTestDocumentService.createOneDocument(ES_TEST_DATA, null, es);
            return MsgUtil.success("添加成功", es);
        } catch (Exception e) {
            if ( (!e.getMessage().contains("201 Created")) && (!e.getMessage().contains("200 OK")) ) {
                e.printStackTrace();
                return MsgUtil.fail("添加失败", e.getMessage());
            } else {
                return MsgUtil.success("添加成功", es);
            }
        }
    }

    @ApiOperation("批量添加新文档")
    @PostMapping("/addNewDocumentByBath")
    public MsgUtil<Object> addNewDocumentByBath(@RequestBody List<EsTest> list) {
        try {
            esTestDocumentService.batchCreate(ES_TEST_DATA, list);
            return MsgUtil.success("添加成功");
        } catch (Exception e) {
            if (!e.getMessage().contains("200 OK")) {
                e.printStackTrace();
                return MsgUtil.fail("添加失败", e.getMessage());
            } else {
                return MsgUtil.success("添加成功");
            }
        }
    }

    @ApiOperation("通过json字符串的方式添加文档")
    @PostMapping("/addNewDocumentByJson")
    public MsgUtil<Object> addNewDocumentByJson(@RequestBody String jsonString) {
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
    public MsgUtil<Object> getDocument(@RequestBody EsQueryDTO<EsTest> dto) {
        try {
            Map<String, Object> esTests;
            dto.setIndexName(ES_TEST_DATA);
            if (StringUtils.isBlank(dto.getQueryString())) {
                esTests = esTestDocumentService.searchByPage(dto);
            } else {
                esTests = esTestDocumentService.searchByQueryString(dto);
            }
            return MsgUtil.success("查询成功", esTests.get("data"), (PageUtil) esTests.get("page"));
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("查询失败", e.getMessage());
        }
    }

    @EnableAsync
    @ApiOperation("多线程添加")
    @PostMapping("/addNewDocumentByBathAndAsync")
    public MsgUtil<Object> addNewDocumentByBathAndAsync(@RequestBody List<EsTest> list) {
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
            @ApiImplicitParam(name = "id", value = "当时返回的检索id", required = true, dataType = "string", paramType = "query", defaultValue = "string类型值")
    })
    public MsgUtil<Object> getAsyncMsg(String id) {
        AsyncMsgUtil asyncMsg = asyncService.findAsyncMsgUtil(id);
        return MsgUtil.success("请求成功", asyncMsg);
    }

    @ApiOperation("测试反射")
    @GetMapping("/test")
    public MsgUtil<Object> test() throws Exception {
        EsTest esTest = new EsTest();
        esTest.setAge(18L);
        esTest.setSex("男");
        EsQueryDTO<EsTest> esQueryDTO = new EsQueryDTO<>();
        esQueryDTO.setIndexName(ES_TEST_DATA);
//        esQueryDTO.setQueryObject(esTest);
        esQueryDTO.setQueryString("为什么");
        esQueryDTO.setPageNum(1);
        esQueryDTO.setPageSize(50);
        Map<String, Object> matchMap = new HashMap<>();
        matchMap.put("name", "zhangsan");
        esQueryDTO.setMatchMap(matchMap);
        Map<String, Object> termMap = new HashMap<>();
        termMap.put("sex", "男");
        esQueryDTO.setTermMap(termMap);
        esQueryDTO.setRangeField("score");
        esQueryDTO.setStartValue("60");
        esQueryDTO.setEndValue("61");
        esQueryDTO.setOrderField("age");
        esQueryDTO.setQueryClazz(EsTest.class);
        Map<String, Object> esTests = esTestDocumentService.searchByQueryObject(esQueryDTO);
        return MsgUtil.success("查询成功", esTests.get("data"), (PageUtil) esTests.get("page"));
    }

    @ApiOperation("通过json创建索引")
    @PostMapping("/createIndexByJson")
    public MsgUtil<Object> createIndexByJson(String indexName, String json) {
        try {
            esIndexService.createIndex(indexName, json);
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("创建失败", e.getMessage());
        }
        return MsgUtil.success();
    }

    @ApiOperation("创建索引")
    @PostMapping("/createIndex")
    public MsgUtil<Object> createIndex(String indexName) {
        try {
            esIndexService.createIndex(indexName);
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("创建失败", e.getMessage());
        }
        return MsgUtil.success();
    }

    @ApiOperation("查看索引是否存在")
    @GetMapping("/isIndexExists")
    public MsgUtil<Object> isIndexExists(String indexName) {
        try {
            boolean result = esIndexService.indexExists(indexName);
            if (result) {
                return MsgUtil.success("索引存在", true);
            } else {
                return MsgUtil.success("索引不存在", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("查询失败", e.getMessage());
        }
    }

    @ApiOperation("删除索引")
    @DeleteMapping("/deleteIndex")
    public MsgUtil<Object> deleteIndex(String indexName) {
        try {
            boolean result = esIndexService.deleteIndex(indexName);
            if (result) {
                return MsgUtil.success("删除成功", true);
            } else {
                return MsgUtil.success("删除失败", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("删除失败", e.getMessage());
        }
    }

}
