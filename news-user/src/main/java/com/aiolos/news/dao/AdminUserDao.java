package com.aiolos.news.dao;

import com.aiolos.news.pojo.AdminUser;
import com.aiolos.news.utils.MyMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserDao extends MyMapper<AdminUser> {
}