package com.mamitang.service.impl;

import com.mamitang.ReturnStatus;
import com.mamitang.dao.ADPictureEntityMapper;
import com.mamitang.dao.AdvertisementEntityMapper;
import com.mamitang.entity.ADPictureEntity;
import com.mamitang.entity.AdvertisementEntity;
import com.mamitang.model.AdvertisementDetail;
import com.mamitang.request.AdvertisementAddRequest;
import com.mamitang.response.RetResponse;
import com.mamitang.service.IAdvertisementService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyy on 10/15/15.
 */
@Service("advertisementService")
public class AdvertisementServiceImpl implements IAdvertisementService {

    @Autowired
    AdvertisementEntityMapper advertisementDao;
    @Autowired
    ADPictureEntityMapper adpictureDao;

    public RetResponse addAdvertisement(AdvertisementAddRequest request) {
        RetResponse result = new RetResponse();
        if(StringUtils.isEmpty(request.getName())){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("广告名不可为空");
            return result;
        }
        AdvertisementEntity advertisement = new AdvertisementEntity();
        advertisement.setName(request.getName());
        advertisement.setPublishtime(new Date());
        advertisementDao.insertAndGetId(advertisement);
        int primaryKey = advertisement.getId();
        if(primaryKey<0){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("发布广告失败");
            return result;
        }
        //判断上传图片是否为空
        if(request.getImgs() !=null){
            List<Map<String , String>> imgs = request.getImgs();
            for(Map<String , String> map : imgs){
                //是否处理entry为空的情况?
                ADPictureEntity pictureEntity = new ADPictureEntity();
                pictureEntity.setAdId(primaryKey);
                pictureEntity.setPath(map.get("path"));
                pictureEntity.setDescription(map.get("description"));
                int flag = adpictureDao.insert(pictureEntity);
                if(flag<0){
                    result.setStatus(ReturnStatus.FAIL);
                    result.setRetMsg("保存图片失败");
                    return result;
                }
            }
        }
        result.setStatus(ReturnStatus.SUCCESS);
        result.setRetMsg("发布广告成功");
        return result;
    }

    public RetResponse getAdvertisementDetail(int id) {
        RetResponse result = new RetResponse();
        if(id<0){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("广告id值无效");
            return result;
        }
        //获取指定id的AdvertisementEntity
        AdvertisementEntity advertisementEntity = advertisementDao.selectByPrimaryKey(id);
        //判断是否为空
        if(advertisementEntity==null){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("该广告不存在");
            return result;
        }
        AdvertisementDetail detail = new AdvertisementDetail();
        detail.setName(advertisementEntity.getName());
        detail.setPublishtime(advertisementEntity.getPublishtime());
        //获取关联的图片信息
        List<ADPictureEntity> list = adpictureDao.getImgsByAID(advertisementEntity.getId());
        //判断该广告下是否有图片信息
        if(list.size()!=0){
            Map<String , String> imgs = new HashMap<String, String>();
            for(ADPictureEntity entity : list){
                imgs.put(entity.getPath() , entity.getDescription());
            }
            detail.setImgs(imgs);
        }
        result.setData(detail);
        result.setStatus(ReturnStatus.SUCCESS);
        return result;
    }

    /**
     * @param page  查第几页
     * @param numOfPage 每页多少条记录
     * @param name  广告主题名称(可选、模糊查询)
     * @param starttime 起始时间(可选)
     * @param endtime   结束时间(可选)
     */
    public RetResponse getAdvertisementList(int page, int numOfPage, String name, Date starttime, Date endtime) {
        RetResponse result = new RetResponse();
        if(page<=0 || numOfPage<1){
            result.setRetMsg("页码参数错误");
            result.setStatus(ReturnStatus.FAIL);
            return result;
        }
        //获取总数
        int count = advertisementDao.getAllAdvertisementsCount();
        //转换总页数，并最终返回
        int countOfPage = (int)Math.ceil((double)count / (double)numOfPage);
        int start = (page-1)*numOfPage;
        //sql条件map
        Map sql_map = new HashMap();
        sql_map.put("start" , start);
        sql_map.put("end" , numOfPage);
        sql_map.put("name" , name);
        //根据时间条件查询
        sql_map.put("starttime" , starttime);
        sql_map.put("endtime" , endtime);

        List<AdvertisementEntity> list = advertisementDao.getAllAdvertisements(sql_map);
        //遍历list，为每个广告实体查询并赋值imgs
        for(AdvertisementEntity ad : list){
            List<ADPictureEntity> imgs = adpictureDao.getImgsByAID(ad.getId());
            if(imgs.size()>0){
                ad.setImgs(imgs);
            }
        }
        //返回结果map,返回总页数和当前列表数据
        Map result_map = new HashMap();
        result_map.put("countOfPage", countOfPage);
        result_map.put("currentList", list);
        result.setData(result_map);
        result.setStatus(ReturnStatus.SUCCESS);
        return result;
    }

    /**
     * @param id    要删除的广告id
     * @return
     */
    public RetResponse deleteAdvertisement(int id) {
        RetResponse result = new RetResponse();
        int effectRow;
        if(id<0){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("广告id值无效");
            return result;
        }
        effectRow = adpictureDao.deleteByAdId(id);
        if(effectRow<0){
            result.setRetMsg("广告删除失败");
            result.setStatus(ReturnStatus.FAIL);
            return result;
        }
        effectRow = advertisementDao.deleteByPrimaryKey(id);
        if(effectRow<0){
            result.setRetMsg("广告删除失败");
            result.setStatus(ReturnStatus.FAIL);
            return result;
        }
        result.setStatus(ReturnStatus.SUCCESS);
        result.setRetMsg("广告删除成功");
        return result;
    }

    /**
     * @param id    要更新的广告id
     * @param request_info  代表请求参数，对应AdvertisementAddRequest类
     * @return
     */
    public RetResponse updateAdvertisement(int id, AdvertisementAddRequest request_info) {
        RetResponse result = new RetResponse();
        int effectRow;
        if(id<0){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("广告id值无效");
            return result;
        }
        AdvertisementEntity new_ad = new AdvertisementEntity();
        new_ad.setId(id);
        new_ad.setName(request_info.getName());
        effectRow = advertisementDao.updateByPrimaryKeySelective(new_ad);
        if(effectRow<0){
            result.setRetMsg("广告更新失败");
            result.setStatus(ReturnStatus.FAIL);
            return result;
        }
        //判断是否有图片信息修改.如果有图片修改,进行更新
        if(request_info.getImgs().size()!=0 && request_info.getImgs()!=null){
            List<Map<String , String>> imgs = request_info.getImgs();
            for(Map<String , String> map : imgs){
                ADPictureEntity new_adp = new ADPictureEntity();
                new_adp.setId(Integer.parseInt(map.get("id")));
                new_adp.setPath(map.get("path"));
                new_adp.setDescription(map.get("description"));
                int flag = adpictureDao.updateByPrimaryKeySelective(new_adp);
                if(flag<0){
                    result.setRetMsg("广告更新失败");
                    result.setStatus(ReturnStatus.FAIL);
                    return result;
                }
            }
        }
        result.setStatus(ReturnStatus.SUCCESS);
        result.setRetMsg("广告更新成功");
        return result;
    }
}












