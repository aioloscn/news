package com.aiolos.news.dao;

import com.aiolos.news.pojo.Comments;
import com.aiolos.news.pojo.vo.CommentSubVO;
import com.aiolos.news.pojo.vo.CommentsVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsDao extends BaseMapper<Comments> {

    IPage<CommentsVO> queryArticleCommentList(Page<CommentsVO> page, @Param("articleId") String articleId);

    List<CommentSubVO> querySubCommentList(@Param("fatherId") String fatherId);
}