package com.aiolos.news.repository;

import com.aiolos.news.pojo.mo.FriendLinkMO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aiolos
 * @date 2020/11/27 6:54 下午
 */
@Repository
public interface FriendLinkRepository extends MongoRepository<FriendLinkMO, String> {
}
