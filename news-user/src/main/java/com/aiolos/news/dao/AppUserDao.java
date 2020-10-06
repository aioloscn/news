package com.aiolos.news.dao;

import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.utils.MyMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserDao extends MyMapper<AppUser> {
}