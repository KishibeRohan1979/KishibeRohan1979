package com.tzp.myWebTest.controller;

import com.tzp.myWebTest.aop.EnableAsync;
import com.tzp.myWebTest.dto.EsQueryDTO;
import com.tzp.myWebTest.dto.MapCreateDTO;
import com.tzp.myWebTest.entity.BilibiliComment;
import com.tzp.myWebTest.service.AsyncService;
import com.tzp.myWebTest.service.BilibiliCommentService;
import com.tzp.myWebTest.service.EsDocumentService;
import com.tzp.myWebTest.service.EsIndexService;
import com.tzp.myWebTest.util.AsyncMsgUtil;
import com.tzp.myWebTest.util.AvidAndBvidUtil;
import com.tzp.myWebTest.util.MsgUtil;
import com.tzp.myWebTest.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/biliComment")
@Api(value = "BilibiliCommentController", tags = "Bili的爬虫测试")
public class BilibiliCommentController {

    @Autowired
    private EsDocumentService<BilibiliComment> esDocumentService;

    @Autowired
    private EsIndexService esIndexService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private BilibiliCommentService bilibiliCommentService;

    @ApiOperation("查询当前爬取进度")
    @PostMapping("/getAsyncMsg")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "当时返回的检索id", required = true, dataType = "string", paramType = "query", defaultValue = "string类型值")
    })
    public MsgUtil<Object> getAsyncMsg(String id) {
        AsyncMsgUtil asyncMsg = asyncService.findAsyncMsgUtil(id);
        return MsgUtil.success("请求成功", asyncMsg);
    }

    @EnableAsync
    @ApiOperation("多线程添加")
    @PostMapping("/addNewDocumentByBathAndAsync")
    public MsgUtil<Object> addNewDocumentByBathAndAsync(@RequestBody MapCreateDTO dto) {
        try {
            System.out.println(dto.getOid());
            Map<String, String> map = bilibiliCommentService.convertMap(dto);
            if ( !"11".equals(dto.getType()) ) {
                String oid = String.valueOf(AvidAndBvidUtil.bvidToAid(dto.getOid()));
                map.put("oid", oid);
            }
            System.out.println(map);
            boolean result = esIndexService.indexExists(dto.getOid());
            if (result) {
                return MsgUtil.fail(1,"该视频下的评论在本网站有缓存");
            }
            esIndexService.createIndex(map.get("oid"));
            bilibiliCommentService.addComment(map);
            return MsgUtil.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("添加失败", e.getMessage());
        }
    }

    @ApiOperation("查询文档")
    @PostMapping("/getDocument")
    public MsgUtil<Object> getDocument(@RequestBody EsQueryDTO<BilibiliComment> dto) {
        try {
            if ( !"11".equals(dto.getType()) ) {
                String oid = String.valueOf(AvidAndBvidUtil.bvidToAid(dto.getIndexName()));
                dto.setIndexName(oid);
            }
            Map<String, Object> comments = esDocumentService.searchByQueryObject(dto);
            return MsgUtil.success("查询成功", comments.get("data"), (PageUtil) comments.get("page"));
        } catch (Exception e) {
            e.printStackTrace();
            return MsgUtil.fail("查询失败", e.getMessage());
        }
    }


}
