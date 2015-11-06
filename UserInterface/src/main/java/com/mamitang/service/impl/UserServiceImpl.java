package com.mamitang.service.impl;

import com.mamitang.MD5Utils;
import com.mamitang.ReturnStatus;
import com.mamitang.dao.UserEntityMapper;
import com.mamitang.entity.UserEntity;
import com.mamitang.model.UserDetail;
import com.mamitang.model.UserRegisterDTO;
import com.mamitang.response.RetResponse;
import com.mamitang.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wyp on 15-7-2.
 */
@Service("userService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserEntityMapper userDao;

    private Logger logger = Logger.getLogger(UserServiceImpl.class);

    public UserEntity login(String userName, String passWord) throws Exception{
        String passStr = MD5Utils.string2MD5(passWord);
        return userDao.selectByUserName(userName, passStr);
    }

    public RetResponse register(UserRegisterDTO request_info) throws Exception{
        RetResponse result = new RetResponse();
        //表单数据项服务端验证
        if(StringUtils.isEmpty(request_info.getUsername())){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("用户名为空");
            return result;
        }
        if(StringUtils.isEmpty(request_info.getPassword())){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("密码为空");
            return result;
        }
        UserEntity user_result = userDao.getUserByName(request_info.getUsername());
        if(user_result!=null){
            result.setStatus(1);
            result.setRetMsg("该用户名已存在");
            return result;
        }
        UserEntity userEntity = new UserEntity();
        //字段赋值开始
        userEntity.setUsername(request_info.getUsername());
        userEntity.setPassword(request_info.getPassword());
        userEntity.setSpecialDish(request_info.getSpecialDish());
        userEntity.setNickname(request_info.getNickname());
        //字段赋值结束
        int effectRow = registerToDao(userEntity);
        if(effectRow < 0){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("用户注册失败");
            return result;
        }
        result.setStatus(ReturnStatus.SUCCESS);
        result.setRetMsg("用户注册成功");
        return result;
    }

    private int registerToDao(UserEntity user) throws Exception{
        //将密码转换成md5
        String passStr = MD5Utils.string2MD5(user.getPassword());
        user.setPassword(passStr);
        return userDao.insert(user);
    }

    public RetResponse getAllUsers(int page , int numOfPage , String querykey , String queryvalue) {
        RetResponse result = new RetResponse();
        if(page<=0 || numOfPage<1){
            result.setRetMsg("页码参数错误");
            result.setStatus(ReturnStatus.FAIL);
            return result;
        }
        //获取总数
        int count = userDao.getAllUsersCount();
        //转换总页数，并最终返回
        int countOfPage = (int) Math.ceil((double) count / (double) numOfPage);
        int start = (page - 1) * numOfPage;
        //sql条件map
        Map sql_map = new HashMap();
        sql_map.put("start", start);
        sql_map.put("end", numOfPage);
        sql_map.put("querykey", querykey);
        sql_map.put("queryvalue", queryvalue);
        List<UserEntity> list = userDao.getAllUsers(sql_map);
        //返回结果map,返回总页数和当前列表数据
        Map result_map = new HashMap();
        result_map.put("countOfPage", countOfPage);
        result_map.put("currentList", list);
        result.setData(result_map);
        result.setStatus(ReturnStatus.SUCCESS);
        return result;
    }

    public RetResponse getUserDetail(int id) {
        RetResponse result = new RetResponse();
        if(id<0){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("用户id值无效");
            return result;
        }
        UserEntity userEntity = userDao.selectByPrimaryKey(id);
        if(userEntity==null){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg("该用户不存在");
            return result;
        }
        //代表用户详情页的实体类
        UserDetail userDetail = new UserDetail();
        //字段赋值开始
        userDetail.setAddress("example_address");
        userDetail.setBank("example_bank");
        userDetail.setCreatetime(userEntity.getCreatetime());
        userDetail.setEmail("example_email");
        userDetail.setIsactive(userEntity.getIsactive());
        userDetail.setNickname(userEntity.getNickname());
        userDetail.setRole("example_role");
        userDetail.setTelephone(userEntity.getUsername());
        //字段赋值结束
        result.setStatus(ReturnStatus.SUCCESS);
        result.setData(userDetail);
        return result;
    }

}
