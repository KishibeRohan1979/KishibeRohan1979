package com.tzp.myWebTest.service.impl;

import com.tzp.myWebTest.dto.MapCreateDTO;
import com.tzp.myWebTest.entity.BilibiliComment;
import com.tzp.myWebTest.service.AsyncService;
import com.tzp.myWebTest.service.BilibiliCommentService;
import com.tzp.myWebTest.service.EsDocumentService;
import com.tzp.myWebTest.util.BiliBiliUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BilibiliCommentServiceImpl implements BilibiliCommentService {

    @Autowired
    private EsDocumentService<BilibiliComment> esTestDocumentService;

    @Autowired
    private AsyncService asyncService;

    private volatile boolean isRunning = false;

    /**
     * 爬取评论
     * @param params map
     */
    @Override
    public void addComment(Map<String, String> params) {
        params.put("sort", "1");
        params.put("ps", "49");
        String countComment = BiliBiliUtil.getCommentsCount(params);
        int totalComment = Integer.parseInt(countComment);
        int totalPage = BiliBiliUtil.getPageInfo(totalComment, 49);
        System.out.println("totalPage:" + totalPage + ",length:" + totalComment);
        for (int i = 1; i <= totalPage; i++) {
            if (isRunning) {
                break;
            }
            params.put("pn", String.valueOf(i));
            String url = BiliBiliUtil.getUrlByMap(BiliBiliUtil.RelyURL, params);
            System.out.println(url);
            try {
                Map<String, Object> map = BiliBiliUtil.getComments(url);
                if ("0".equals(map.get("code"))) {
                    boolean isOver = Boolean.parseBoolean(String.valueOf(map.get("isOver")));
                    if (isOver) {
                        System.out.println("发现已经没有内容了");
                        break;
                    } else {
                        List<BilibiliComment> resultList = (List<BilibiliComment>)map.get("resultList");
                        esTestDocumentService.batchCreate(params.get("oid"), resultList);
                        //计算百分比
                        String per = String.valueOf( ((double) i/totalPage)*100 );
                        String[] point = per.split("\\.");
                        String beforePoint = point[0];
                        String afterPoint = point[1] + "0";
                        //更新redis缓存任务进度
                        asyncService.updateProgress(beforePoint + "." + afterPoint.substring(0,2) + "%");
                    }
                } else {
                    System.out.println("第" + i + "页爬取失败，code=" + map.get("code") + "，" + map.get("requestMessage"));
                    i--;
                }
            } catch (IOException e) {
                i--;
                asyncService.updateMsg("爬取暂时被拦截，这可能需要3分钟时间恢复进度...");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("添加失败");
                i--;
                e.printStackTrace();
            }
        }
    }

    /**
     * 把MapCreateDTO转为map
     * @param dto 创建类
     * @throws IllegalAccessException 转化错误
     */
    @Override
    public Map<String, String> convertMap(MapCreateDTO dto) throws IllegalAccessException {
        Map<String, String> result = new HashMap<>();
        Class<?> clazz = dto.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String fieldValue = field.get(dto).toString();
            result.put(fieldName, fieldValue);
        }
        return result;
    }

    /**
     * 强制停止爬取
     */
    @Override
    public void stop() {
        isRunning = true;
    }

    /**
     * 强制启动
     */
    @Override
    public void start() {
        isRunning = false;
    }

}
