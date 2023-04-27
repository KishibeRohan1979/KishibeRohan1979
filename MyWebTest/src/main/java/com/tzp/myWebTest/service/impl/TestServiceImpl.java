package com.tzp.myWebTest.service.impl;

import com.tzp.myWebTest.entity.EsTest;
import com.tzp.myWebTest.service.AsyncService;
import com.tzp.myWebTest.service.EsDocumentService;
import com.tzp.myWebTest.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private EsDocumentService<EsTest> esTestDocumentService;

    @Autowired
    private AsyncService asyncService;

    @Override
    public void demo(List<EsTest> esTests) {
        for (int i = 0; i < esTests.size(); i++) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //计算百分比
            String per = String.valueOf( ((double) i/esTests.size())*100 );
            String[] point = per.split("\\.");
            String beforePoint = point[0];
            String afterPoint = point[1] + "0";
            //更新redis缓存任务进度
            asyncService.updateProgress(beforePoint + "." + afterPoint.substring(0,2) + "%");
            EsTest esTest = esTests.get(i);
            try {
                esTestDocumentService.createOneDocument("es_test_data", null, esTest);
            } catch (Exception e) {
                if ( (!e.getMessage().contains("201 Created")) && (!e.getMessage().contains("200 OK")) ) {
                    e.printStackTrace();
                }
            }

        }
    }
}
