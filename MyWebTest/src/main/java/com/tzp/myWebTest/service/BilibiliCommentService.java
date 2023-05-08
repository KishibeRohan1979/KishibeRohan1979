package com.tzp.myWebTest.service;

import com.tzp.myWebTest.dto.EsQueryDTO;
import com.tzp.myWebTest.dto.MapCreateDTO;
import com.tzp.myWebTest.entity.BilibiliComment;

import java.util.List;
import java.util.Map;

public interface BilibiliCommentService {

    void addComment(Map<String, String> params);

    Map<String, String> convertMap(MapCreateDTO dto) throws IllegalAccessException;

}
