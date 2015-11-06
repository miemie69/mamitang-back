package com.mamitang.action;

import com.alibaba.fastjson.JSON;
import com.mamitang.ReturnStatus;
import com.mamitang.request.AdvertisementAddRequest;
import com.mamitang.response.RetResponse;
import com.mamitang.service.IAdvertisementService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 广告管理Action
 * Created by lyy on 10/14/15.
 */
@RestController
@RequestMapping("/advertisementinfo")
public class AdvertisementAction {

    @Autowired
    IAdvertisementService advertisementService;

    /**
     * 新增一个广告
     * @param paraminfo     代表请求参数，对应AdvertisementAddRequest类
     * @return
     */
    @RequestMapping(value = "add" , method = RequestMethod.POST)
    @ResponseBody
    public RetResponse addAdvertisement(@RequestBody String paraminfo){
        RetResponse result = new RetResponse();
        try {
            AdvertisementAddRequest request_info = JSON.parseObject(paraminfo, AdvertisementAddRequest.class);
            result = advertisementService.addAdvertisement(request_info);
        }catch (Exception e) {
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 分页查询广告列表
     * @param page  查第几页
     * @param numOfPage 每页多少条记录
     * @return
     */
    @RequestMapping(value = "/advertisements/{page}/to/{numOfPage}" , method = RequestMethod.GET)
    @ResponseBody
    public RetResponse showAdvertisements(@PathVariable("page") int page ,
                                          @PathVariable("numOfPage") int numOfPage ,
                                          @RequestParam(value = "name" , required = false) String name ,
                                          @RequestParam(value = "starttime" , required = false) String starttime ,
                                          @RequestParam(value = "endtime" , required = false) String endtime){
        RetResponse result = new RetResponse();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start_time = null;
        Date end_time = null;
        try {
            //将字符串转换成日期类型
            if(!StringUtils.isEmpty(starttime)){
                start_time = sdf.parse(starttime);
            }
            if(!StringUtils.isEmpty(endtime)){
                end_time = sdf.parse(endtime);
            }
            result = advertisementService.getAdvertisementList(page, numOfPage, name, start_time , end_time);
        }catch (Exception e){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 根据id查询广告详情页
     * @param id    广告id
     * @return
     */
    @RequestMapping(value = "show/{id}" , method = RequestMethod.GET)
    @ResponseBody
    public RetResponse showAdvertisement(@PathVariable("id") int id){
        RetResponse result = new RetResponse();
        try {
            result = advertisementService.getAdvertisementDetail(id);
        }catch (Exception e){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 根据id删除广告
     * @param id
     * @return
     */
    @RequestMapping(value = "delete/{id}" , method = RequestMethod.GET)
    @ResponseBody
    public RetResponse deleteAdvertisement(@PathVariable("id") int id){
        RetResponse result = new RetResponse();
        try {
            result = advertisementService.deleteAdvertisement(id);
        }catch (Exception e){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 根据id更新广告
     * @param id            要更新的广告id
     * @param paraminfo     代表请求参数，对应AdvertisementAddRequest类
     * @return
     */
    @RequestMapping(value = "update/{id}" , method = RequestMethod.POST)
    @ResponseBody
    public RetResponse updateAdvertisement(@PathVariable("id") int id , @RequestBody String paraminfo){
        RetResponse result = new RetResponse();
        try {
            AdvertisementAddRequest request_info = JSON.parseObject(paraminfo, AdvertisementAddRequest.class);
            result = advertisementService.updateAdvertisement(id , request_info);
        }catch (Exception e){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg(e.getMessage());
        }
        return result;
    }
}
















