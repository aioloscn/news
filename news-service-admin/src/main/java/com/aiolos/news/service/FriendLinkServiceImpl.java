package com.aiolos.news.service;

import com.aiolos.news.pojo.mo.FriendLinkMO;
import com.aiolos.news.repository.FriendLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public void saveOrUpdateFriendLink(FriendLinkMO friendLinkMO) {
        friendLinkRepository.save(friendLinkMO);
    }
}
