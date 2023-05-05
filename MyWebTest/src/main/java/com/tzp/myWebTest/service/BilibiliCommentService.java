package com.tzp.myWebTest.service;

import com.tzp.myWebTest.dto.EsQueryDTO;
import com.tzp.myWebTest.entity.BilibiliComment;

import java.util.List;

public interface BilibiliCommentService {

    void addComment(List<BilibiliComment> comments, EsQueryDTO<BilibiliComment> dto);

}
