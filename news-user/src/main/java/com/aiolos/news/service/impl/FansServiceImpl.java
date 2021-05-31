package com.aiolos.news.service.impl;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.enums.Sex;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.dao.FansDao;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.pojo.Fans;
import com.aiolos.news.pojo.vo.FansCountsVO;
import com.aiolos.news.pojo.vo.RegionRatioVO;
import com.aiolos.news.service.BaseService;
import com.aiolos.news.service.FansService;
import com.aiolos.news.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Aiolos
 * @date 2021/5/13 5:32 上午
 */
@Service
public class FansServiceImpl extends BaseService implements FansService {

    private final FansDao fansDao;

    private final UserService userService;

    public FansServiceImpl(FansDao fansDao, UserService userService) {
        this.fansDao = fansDao;
        this.userService = userService;
    }

    @Override
    public void isMeFollowThisWriter(String writerId, String fanId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("writer_id", writerId);
        queryWrapper.eq("fan_id", fanId);
        Fans fans = fansDao.selectOne(queryWrapper);
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void follow(String writerId, String fanId) throws CustomizedException {
        AppUser fanInfo = userService.getUser(fanId);
        Fans fans = new Fans();
        fans.setId(String.valueOf(idWorker.nextId()));
        fans.setWriterId(writerId);
        fans.setFanId(fanId);
        fans.setFace(fanInfo.getFace());
        fans.setFanNickname(fanInfo.getNickname());
        fans.setSex(fanInfo.getSex());
        fans.setProvince(fanInfo.getProvince());
        int result = fansDao.insert(fans);
        if (result != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new CustomizedException(ErrorEnum.FOLLOW_FAILED);
            }
        }

        // 作家粉丝数累加
        redis.increment(REDIS_WRITER_FANS_COUNT + ":" + writerId, 1);
        // 当前用户的关注数累加
        redis.increment(REDIS_MY_FOLLOW_COUNT + ":" + fanId, 1);
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void unfollow(String writerId, String fanId) throws CustomizedException {
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("writer_id", writerId);
        wrapper.eq("fan_id", fanId);
        int result = fansDao.delete(wrapper);
        if (result == 0) {
            throw new CustomizedException(ErrorEnum.UNFOLLOW_FAILED);
        }
        // 作家粉丝数累减
        redis.decrement(REDIS_WRITER_FANS_COUNT + ":" + writerId, 1);
        // 当前用户的关注数累减
        redis.decrement(REDIS_MY_FOLLOW_COUNT + ":" + fanId, 1);
    }

    @Override
    public PagedResult queryFansList(String writerId, Integer page, Integer pageSize) {
        IPage<Fans> fansPage = new Page<>(page, pageSize);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("writer_id", writerId);
        fansPage = fansDao.selectPage(fansPage, queryWrapper);
        PagedResult pagedResult = setterPagedResult(fansPage);
        return pagedResult;
    }

    @Override
    public FansCountsVO queryFansCounts(String writerId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("writer_id", writerId);
        List<Fans> fansList = fansDao.selectList(queryWrapper);
        Map<Integer, Long> fansCountsMap = fansList.stream().collect(Collectors.groupingBy(Fans::getSex, Collectors.counting()));
        FansCountsVO fansCountsVO = new FansCountsVO();
        fansCountsVO.setManCounts(fansCountsMap.get(Sex.man.getType()).intValue());
        fansCountsVO.setWomanCounts(fansCountsMap.get(Sex.woman.getType()).intValue());
        return fansCountsVO;
    }

    @Override
    public List<RegionRatioVO> queryRatioByRegion(String writerId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("writer_id", writerId);
        List<Fans> fansList = fansDao.selectList(queryWrapper);
        Map<String, Long> fansCountsMap = fansList.stream().collect(Collectors.groupingBy(Fans::getProvince, Collectors.counting()));
        List<String> provinces = fansCountsMap.keySet().stream().collect(Collectors.toList());
        List<RegionRatioVO> regionRatioVOs = new ArrayList<>();
        provinces.forEach(p -> {
            RegionRatioVO vo = new RegionRatioVO();
            Long count = fansCountsMap.get(p);
            vo.setName(p);
            vo.setValue(count.intValue());
            regionRatioVOs.add(vo);
        });
        return regionRatioVOs;
    }
}
