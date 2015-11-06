package com.mamitang.service.impl;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import com.mamitang.JPushUtils;
import com.mamitang.ReturnStatus;
import com.mamitang.dao.HallEntityMapper;
import com.mamitang.dao.UserEntityMapper;
import com.mamitang.entity.HallEntity;
import com.mamitang.model.HallDetail;
import com.mamitang.response.RetResponse;
import com.mamitang.service.IHallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyy on 11/2/15.
 */
@Service("hallService")
public class HallServiceImpl implements IHallService{

    @Autowired
    HallEntityMapper hallDao;
    @Autowired
    UserEntityMapper userDao;

    public RetResponse getHallDetail(int id) {
        RetResponse result = new RetResponse();
        if (id < 0) {
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("堂id值无效");
            return result;
        }
        //获取指定id的HallEntity
        HallEntity hallEntity = hallDao.selectByPrimaryKey(id);
        if (hallEntity == null) {
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("该堂不存在");
            return result;
        }
        //代表堂详情页的实体
        HallDetail hallDetail = new HallDetail();
        //字段赋值开始
        hallDetail.setHallName(hallEntity.getHallName());
        hallDetail.setHallSupplyType(hallEntity.getHallSupplyType());
        hallDetail.setRoll("堂主");
        hallDetail.setHallPhone(hallEntity.getHallPhone());
        hallDetail.setHallPraiseNum(hallEntity.getHallPraiseNum());
        hallDetail.setHallCollectionNum(hallEntity.getHallCollectionNum());
        hallDetail.setHallAddress(hallEntity.getHallAddress());
        hallDetail.setHallCreateDate(hallEntity.getHallCreateDate());
        hallDetail.setHallCoverUrl(hallEntity.getHallCoverUrl());
        //字段赋值结束
        result.setData(hallDetail);
        result.setStatus(ReturnStatus.SUCCESS);
        return result;
    }

    /**
     * @param page          查第几页
     * @param numOfPage     每页多少条记录
     * @param querykey      查询条件(可选)
     * @param queryvalue    查询值(可选)
     * @param start_time    时间条件查询(可选,起始时间)
     * @param end_time      时间条件查询(可选,终止时间)
     * @return
     */
    public RetResponse getHallList(int page, int numOfPage, String querykey, String queryvalue, Date start_time, Date end_time) {
        RetResponse result = new RetResponse();
        if (page <= 0 || numOfPage < 1) {
            result.setRetMsg("页码参数错误");
            result.setStatus(ReturnStatus.FAIL);
            return result;
        }
        //获取总数
        int count = hallDao.getAllHallsCount();
        //转换总页数，并最终返回
        int countOfPage = (int) Math.ceil((double) count / (double) numOfPage);
        int start = (page - 1) * numOfPage;
        //sql条件map
        Map sql_map = new HashMap();
        sql_map.put("start", start);
        sql_map.put("end", numOfPage);
        sql_map.put("querykey", querykey);
        if (querykey!=null && querykey.equals("hallSupplyType")){
            //对查询条件进行判断和转换
            if(queryvalue!=null && queryvalue!=""){
                int convertValue = Integer.parseInt(queryvalue);
                if(!(convertValue==0 || convertValue==1)){
                    result.setRetMsg("查询参数错误");
                    result.setStatus(ReturnStatus.FAIL);
                    return result;
                }
                sql_map.put("queryvalue", convertValue);
            }else {
                sql_map.put("queryvalue",queryvalue);
            }
        }else{
            sql_map.put("queryvalue",queryvalue);
        }
        //根据时间条件查询
        sql_map.put("starttime" , start_time);
        sql_map.put("endtime" , end_time);
        List<HallEntity> list = hallDao.getAllHalls(sql_map);
        //返回结果map,返回总页数和当前列表数据
        Map result_map = new HashMap();
        result_map.put("countOfPage", countOfPage);
        result_map.put("currentList", list);
        result.setData(result_map);
        result.setStatus(ReturnStatus.SUCCESS);
        return result;
    }

    /**
     * @param id
     * @return
     */
    public RetResponse changeHallState(int id) throws Exception{
        RetResponse result = new RetResponse();
        int effectRow;
        if (id < 0) {
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("堂id值无效");
            return result;
        }
        //获取指定id的HallEntity
        HallEntity hallEntity = hallDao.selectByPrimaryKey(id);
        if (hallEntity == null) {
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("该堂不存在");
            return result;
        }
        //获取堂的用户id
        int userId = hallEntity.getUid();
        if(userId<0){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("参数错误");
            return result;
        }
        //根据用户id获取用户名
        String userName = userDao.getUserNameById();
        if (userName==null || userName.trim()==""){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("获取用户名失败");
            return result;
        }
        //判断堂状态!=0
        int hall_state = hallEntity.getHallState();
        if (hall_state!=0){
            hallEntity.setHallState(0);
            effectRow = hallDao.updateByPrimaryKeySelective(hallEntity);
            if(effectRow<0){
                result.setRetMsg("堂更新失败");
                result.setStatus(ReturnStatus.FAIL);
                return result;
            }
            result.setStatus(ReturnStatus.SUCCESS);
            result.setRetMsg("堂关闭成功");
            return result;
        }else {
            hallEntity.setHallState(1);
            effectRow = hallDao.updateByPrimaryKeySelective(hallEntity);
            if(effectRow<0){
                result.setRetMsg("堂更新失败");
                result.setStatus(ReturnStatus.FAIL);
                return result;
            }
            //如果堂开启成功,则调用JPushAPI为用户推送开启堂成功信息,构建JPushClient
//            JPushClient client = JPushUtils.getClient();
//            PushPayload payload = PushPayload.newBuilder()
//                    .setPlatform(Platform.all())
//                    .setAudience(Audience.alias(userName))    //推送别名为用户名
//                    .setNotification(Notification.alert("您的堂已开启"))
//                    .build();
//            PushResult pushResult = client.sendPush(payload);
            result.setStatus(ReturnStatus.SUCCESS);
            result.setRetMsg("堂开启成功");
            return result;
        }
    }

}