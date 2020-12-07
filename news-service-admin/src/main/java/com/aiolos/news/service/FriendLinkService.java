package com.aiolos.news.service;

import com.aiolos.news.pojo.mo.FriendLinkMO;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/27 8:03 下午
 */
public interface FriendLinkService {

    /**
     * 新增或更新友情链接
     * @param friendLinkMO
     */
    void saveOrUpdateFriendLink(FriendLinkMO friendLinkMO);

    /**
     * 查询所有友情链接
     * @return
     */
    List<FriendLinkMO> queryAllFriendLinks();

    void delete(String linkId);

    List<FriendLinkMO> queryPortalAllFriendLinkList();
}
