package com.aiolos.news.dao;

import com.aiolos.news.pojo.Comments;
import com.aiolos.news.utils.MyMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsDao extends MyMapper<Comments> {
}