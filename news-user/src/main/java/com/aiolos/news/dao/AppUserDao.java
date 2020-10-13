package com.aiolos.news.dao;

import com.aiolos.news.pojo.AppUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserDao extends BaseMapper<AppUser> {
}