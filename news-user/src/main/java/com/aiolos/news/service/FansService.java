package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizeException;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.pojo.vo.FansCountsVO;
import com.aiolos.news.pojo.vo.RegionRatioVO;

import java.util.List;

/**
 * @author Aiolos
 * @date 2021/5/13 5:32 上午
 */
public interface FansService {

    /**
     * 查询当前用户是否关注了该作家
     * @param writerId
     * @param fanId
     */
    void isMeFollowThisWriter(String writerId, String fanId);

    /**
     * 用户关注作家
     * @param writerId
     * @param fanId
     * @throws CustomizeException
     */
    void follow(String writerId, String fanId) throws CustomizeException;

    /**
     * 用户取消关注
     * @param writerId
     * @param fanId
     */
    void unfollow(String writerId, String fanId) throws CustomizeException;

    /**
     * 查询粉丝分页列表
     * @param writerId
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult queryFansList(String writerId, Integer page, Integer pageSize);

    /**
     * 查询男女粉丝数量
     * @param writerId
     * @return
     */
    FansCountsVO queryFansCounts(String writerId);

    List<RegionRatioVO> queryRatioByRegion(String writerId);
}
