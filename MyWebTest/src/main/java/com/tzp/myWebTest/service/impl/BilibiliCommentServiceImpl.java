package com.tzp.myWebTest.service.impl;

import com.tzp.myWebTest.dto.EsQueryDTO;
import com.tzp.myWebTest.entity.BilibiliComment;
import com.tzp.myWebTest.service.AsyncService;
import com.tzp.myWebTest.service.BilibiliCommentService;
import com.tzp.myWebTest.service.EsDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BilibiliCommentServiceImpl implements BilibiliCommentService {

    @Autowired
    private EsDocumentService<BilibiliComment> esTestDocumentService;

    @Autowired
    private AsyncService asyncService;

    @Override
    public void addComment(List<BilibiliComment> comments, EsQueryDTO<BilibiliComment> dto) {
        for (int i = 0; i < comments.size(); i++) {

            //计算百分比
            String per = String.valueOf( ((double) i/comments.size())*100 );
            String[] point = per.split("\\.");
            String beforePoint = point[0];
            String afterPoint = point[1] + "0";
            //更新redis缓存任务进度
            asyncService.updateProgress(beforePoint + "." + afterPoint.substring(0,2) + "%");
            BilibiliComment comment = comments.get(i);
            try {
                esTestDocumentService.createOneDocument(dto.getIndexName(), null, comment);
            } catch (Exception e) {
                if ( (!e.getMessage().contains("201 Created")) && (!e.getMessage().contains("200 OK")) ) {
                    e.printStackTrace();
                }
            }
        }
    }

}
