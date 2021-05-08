package com.aiolos.news.repository;

import com.aiolos.news.pojo.mo.FriendLinkMO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/27 6:54 下午
 */
@Repository
public interface FriendLinkRepository extends MongoRepository<FriendLinkMO, String> {

    /**
     * 自定义查询，根据isDelete条件查询所有相关数据，类似于jpa的写法，不需要自己写实现
     * @param isDelete
     * @return
     */
    List<FriendLinkMO> getAllByIsDelete(Integer isDelete);
}
