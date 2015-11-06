package com.mamitang.service.impl;

import com.mamitang.ReturnStatus;
import com.mamitang.dao.FoodEntityMapper;
import com.mamitang.dao.HallEntityMapper;
import com.mamitang.entity.FoodEntity;
import com.mamitang.entity.HallEntity;
import com.mamitang.model.FoodDetail;
import com.mamitang.request.FoodUpdateRequest;
import com.mamitang.response.RetResponse;
import com.mamitang.service.IFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyy on 10/29/15.
 */
@Service("foodService")
public class FoodServiceImpl implements IFoodService {

    @Autowired
    FoodEntityMapper foodDao;
    @Autowired
    HallEntityMapper hallDao;

    public RetResponse getFoodDetail(int id) {
        RetResponse result = new RetResponse();
        if (id < 0) {
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("菜品id值无效");
            return result;
        }
        //获取指定id的FoodEntity
        FoodEntity foodEntity = foodDao.selectByPrimaryKey(id);
        if (foodEntity == null) {
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("该菜品不存在");
            return result;
        }
        //根据hid获取堂信息
        HallEntity hallEntity = hallDao.selectByPrimaryKey(foodEntity.getHid());
        if (hallEntity == null) {
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("该堂不存在");
            return result;
        }
        //代表菜品详情页的实体
        FoodDetail detail = new FoodDetail();
        //字段赋值开始
        detail.setFoodName(foodEntity.getFoodName());
        detail.setHallName(hallEntity.getHallName());
        detail.setFoodState(foodEntity.getFoodState());
        detail.setHallPhone(hallEntity.getHallPhone());
        detail.setHallAddress(hallEntity.getHallAddress());
        detail.setFoodSupplyType(foodEntity.getFoodSupplyType());
        detail.setFoodSupplyCycle(foodEntity.getFoodSupplyCycle());
        detail.setFoodBeginSupplyDate(foodEntity.getFoodBeginSupplyDate());
        detail.setFoodEndSupplyDate(foodEntity.getFoodEndSupplyDate());
        detail.setFoodPrice(foodEntity.getFoodPrice());
        detail.setFoodSupplyNum(foodEntity.getFoodSupplyNum());
        detail.setFoodPictureUrl(foodEntity.getFoodPictureUrl());
        detail.setFoodIntroduce(foodEntity.getFoodIntroduce());
        //字段赋值结束
        result.setData(detail);
        result.setStatus(ReturnStatus.SUCCESS);
        return result;
    }

    /**
     * @param page       查第几页
     * @param numOfPage  每页多少条记录
     * @param querykey   查询条件(可选)
     * @param queryvalue 查询值(可选)
     * @return
     */
    public RetResponse getFoodList(int page, int numOfPage, String querykey, String queryvalue , Date start_time , Date end_time){
        RetResponse result = new RetResponse();
        if (page <= 0 || numOfPage < 1) {
            result.setRetMsg("页码参数错误");
            result.setStatus(ReturnStatus.FAIL);
            return result;
        }
        //获取总数
        int count = foodDao.getAllAdvertisementsCount();
        //转换总页数，并最终返回
        int countOfPage = (int) Math.ceil((double) count / (double) numOfPage);
        int start = (page - 1) * numOfPage;
        //sql条件map
        Map sql_map = new HashMap();
        sql_map.put("start", start);
        sql_map.put("end", numOfPage);
        sql_map.put("querykey" , querykey);
        //对查询条件进行判断和转换
        if(queryvalue!=null && queryvalue!=""){
            //如果查询值不为空，校验并转换
            int i = Integer.parseInt(queryvalue);
            if(!(i==0 || i==1)){
                result.setRetMsg("the error query parameters");
                result.setStatus(ReturnStatus.FAIL);
                return result;
            }
            sql_map.put("queryvalue", i);
        }else {
            sql_map.put("queryvalue" , queryvalue);
        }
        //根据时间条件查询
        sql_map.put("starttime" , start_time);
        sql_map.put("endtime" , end_time);

        List<FoodEntity> list = foodDao.getAllFoods(sql_map);

        for(FoodEntity entity : list){
            HallEntity hallEntity = hallDao.selectByPrimaryKey(entity.getHid());
            entity.setHallName(hallEntity.getHallName());
            entity.setTelephone(hallEntity.getHallPhone());
        }
        //返回结果map,返回总页数和当前列表数据
        Map result_map = new HashMap();
        result_map.put("countOfPage", countOfPage);
        result_map.put("currentList", list);
        result.setData(result_map);
        result.setStatus(ReturnStatus.SUCCESS);
        return result;
    }

    public RetResponse updateFood(int id, FoodUpdateRequest request_info) {
        RetResponse result = new RetResponse();
        int effectRow;
        if(id<0){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("菜品id值无效");
            return result;
        }
        FoodEntity new_food = new FoodEntity();
        //字段赋值开始
        new_food.setId(id);
        new_food.setFoodName(request_info.getFoodName());
        new_food.setFoodState(request_info.getFoodState());
        new_food.setFoodSupplyType(request_info.getFoodSupplyType());
        new_food.setFoodSupplyCycle(request_info.getFoodSupplyCycle());
        new_food.setFoodBeginSupplyDate(request_info.getFoodBeginSupplyDate());
        new_food.setFoodEndSupplyDate(request_info.getFoodEndSupplyDate());
        new_food.setFoodPrice(request_info.getFoodPrice());
        new_food.setFoodSupplyNum(request_info.getFoodSupplyNum());
        new_food.setFoodPictureUrl(request_info.getFoodPictureUrl());
        new_food.setFoodIntroduce(request_info.getFoodIntroduce());
        //字段赋值结束
        effectRow = foodDao.updateByPrimaryKeySelective(new_food);
        if(effectRow<0){
            result.setRetMsg("菜品更新失败");
            result.setStatus(ReturnStatus.FAIL);
            return result;
        }
        result.setStatus(ReturnStatus.SUCCESS);
        result.setRetMsg("菜品更新成功");
        return result;
    }

}





















