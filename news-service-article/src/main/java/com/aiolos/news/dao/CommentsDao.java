package com.aiolos.news.dao;

import com.aiolos.news.pojo.Comments;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsDao extends BaseMapper<Comments> {
}