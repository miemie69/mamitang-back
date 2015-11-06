package com.mamitang.dao;

import com.mamitang.DaoMaster;
import com.mamitang.entity.AdvertisementEntity;

import java.util.List;
import java.util.Map;

@DaoMaster
public interface AdvertisementEntityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AdvertisementEntity record);

    int insertSelective(AdvertisementEntity record);

    AdvertisementEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AdvertisementEntity record);

    int updateByPrimaryKey(AdvertisementEntity record);

    int insertAndGetId(AdvertisementEntity record);

    int getAllAdvertisementsCount();

    List<AdvertisementEntity> getAllAdvertisements(Map sql_map);
}