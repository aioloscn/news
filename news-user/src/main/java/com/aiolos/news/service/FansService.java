package com.aiolos.news.service;

import com.aiolos.news.common.exception.CustomizedException;
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
     * @param writerId 作家Id
     * @param fanId 粉丝Id
     */
    boolean isMeFollowThisWriter(String writerId, String fanId);

    /**
     * 用户关注作家
     * @param writerId 作家Id
     * @param fanId 粉丝Id
     * @throws CustomizedException
     */
    void follow(String writerId, String fanId) throws CustomizedException;

    /**
     * 用户取消关注
     * @param writerId 作家Id
     * @param fanId 粉丝Id
     */
    void unfollow(String writerId, String fanId) throws CustomizedException;

    /**
     * 查询粉丝分页列表
     * @param writerId 作家Id
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult queryFansList(String writerId, Integer page, Integer pageSize);

    /**
     * 从ES中查询粉丝分页列表
     * @param writerId 作家Id
     * @param page 第几页
     * @param pageSize 每页显示数
     * @return
     */
    PagedResult queryFansESList(String writerId, Integer page, Integer pageSize);

    /**
     * 查询男女粉丝数量
     * @param writerId 作家Id
     * @return
     */
    FansCountsVO queryFansCounts(String writerId);

    /**
     * 查询作家每个省份的粉丝比例
     * @param writerId 作家Id
     * @return
     */
    List<RegionRatioVO> queryRatioByRegion(String writerId);

    /**
     * 更新该作家在ES中的某个粉丝关联关系数据
     * @param relationId 粉丝关联关系表主键
     * @param fanId 粉丝Id
     */
    void forceUpdateFanInfo(String relationId, String fanId);
}
