package com.aiolos.news.service.impl;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.enums.Sex;
import com.aiolos.news.common.exception.CustomizedException;
import com.aiolos.news.common.utils.PagedResult;
import com.aiolos.news.dao.FansDao;
import com.aiolos.news.pojo.AppUser;
import com.aiolos.news.pojo.Fans;
import com.aiolos.news.pojo.eo.FansEO;
import com.aiolos.news.pojo.vo.FansCountsVO;
import com.aiolos.news.pojo.vo.RegionRatioVO;
import com.aiolos.news.service.BaseService;
import com.aiolos.news.service.FansService;
import com.aiolos.news.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Aiolos
 * @date 2021/5/13 5:32 上午
 */
@Slf4j
@Service
public class FansServiceImpl extends BaseService implements FansService {

    private final FansDao fansDao;

    private final UserService userService;

    private final ElasticsearchTemplate elasticsearchTemplate;

    public FansServiceImpl(FansDao fansDao, UserService userService, ElasticsearchTemplate elasticsearchTemplate) {
        this.fansDao = fansDao;
        this.userService = userService;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public boolean isMeFollowThisWriter(String writerId, String fanId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("writer_id", writerId);
        queryWrapper.eq("fan_id", fanId);
        Fans fans = fansDao.selectOne(queryWrapper);
        return fans != null;
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

        // 保存粉丝关系到es中
        FansEO fansEO = new FansEO();
        BeanUtils.copyProperties(fans, fansEO);
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(fansEO).build();
        String index = elasticsearchTemplate.index(indexQuery);
        log.info("保存粉丝关系，作家: {}，粉丝: {}, ES索引: {}", writerId, fanId, index);
        if (StringUtils.isBlank(index)) {
            log.error("保存粉丝关系，作家: {}，粉丝: {}，保存ES索引失败", writerId, fanId);
        }
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

        // 删除es中的粉丝关系
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(QueryBuilders.termQuery("writerId", writerId));
        deleteQuery.setQuery(QueryBuilders.termQuery("fanId", fanId));
        elasticsearchTemplate.delete(deleteQuery, FansEO.class);
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
    public PagedResult queryFansESList(String writerId, Integer page, Integer pageSize) {
        // es的页面是从0开始计算的，所以在这里page需要-1
        if (page < 1) return null;
        page--;
        // 分页
        Pageable pageable = PageRequest.of(page, pageSize);
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.termQuery("writerId", writerId)).withPageable(pageable).build();
        AggregatedPage<FansEO> pagedFans = elasticsearchTemplate.queryForPage(query, FansEO.class);

        IPage<FansEO> fansIPage = new Page<>();
        fansIPage.setRecords(pagedFans.getContent());
        fansIPage.setCurrent(++page);
        fansIPage.setPages(pagedFans.getTotalPages());
        fansIPage.setTotal(pagedFans.getTotalElements());
        return setterPagedResult(fansIPage);
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

    @Transactional(propagation = Propagation.NESTED, rollbackFor = CustomizedException.class)
    @Override
    public void forceUpdateFanInfo(String relationId, String fanId) {
        // 根据fanId查询用户信息
        AppUser fan = userService.getUser(fanId);
        // 更新用户信息到db和es中
        Fans fans = new Fans();
        fans.setId(relationId);
        fans.setFanId(fanId);
        fans.setFace(fan.getFace());
        fans.setFanNickname(fan.getNickname());
        fans.setSex(fan.getSex());
        fans.setProvince(fan.getProvince());
        int result = fansDao.updateById(fans);
        if (result != 1) {
            throw new RuntimeException();
        }
        // 更新到es中
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("face", fan.getFace());
        updateMap.put("fanNickname", fan.getNickname());
        updateMap.put("sex", fan.getSex());
        updateMap.put("province", fan.getProvince());
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(indexRequest);
        UpdateQuery updateQuery = new UpdateQueryBuilder().withClass(FansEO.class).withId(relationId).withIndexRequest(indexRequest).build();
        elasticsearchTemplate.update(updateQuery);
    }
}
