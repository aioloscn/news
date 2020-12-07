package com.aiolos.news.service.impl;

import com.aiolos.news.common.enums.YesOrNo;
import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.pojo.mo.FriendLinkMO;
import com.aiolos.news.repository.FriendLinkRepository;
import com.aiolos.news.service.FriendLinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/27 8:04 下午
 */
@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    public final FriendLinkRepository friendLinkRepository;

    public FriendLinkServiceImpl(FriendLinkRepository friendLinkRepository) {
        this.friendLinkRepository = friendLinkRepository;
    }

    @Override
    @Transactional(rollbackFor = CustomizeException.class)
    public void saveOrUpdateFriendLink(FriendLinkMO friendLinkMO) {
        friendLinkRepository.save(friendLinkMO);
    }

    @Override
    public List<FriendLinkMO> queryAllFriendLinks() {
        return friendLinkRepository.findAll();
    }

    @Override
    public void delete(String linkId) {
        friendLinkRepository.deleteById(linkId);
    }

    @Override
    public List<FriendLinkMO> queryPortalAllFriendLinkList() {
        return friendLinkRepository.getAllByIsDelete(YesOrNo.NO.type);
    }
}
