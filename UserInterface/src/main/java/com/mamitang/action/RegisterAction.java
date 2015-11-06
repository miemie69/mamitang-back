package com.mamitang.action;

import com.alibaba.fastjson.JSON;
import com.mamitang.ReturnStatus;
import com.mamitang.entity.UserEntity;
import com.mamitang.model.UserRegisterDTO;
import com.mamitang.request.AdvertisementAddRequest;
import com.mamitang.response.RetResponse;
import com.mamitang.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 注册Action
 * Created by lyy on 15-9-23.
 */
@Controller
@RequestMapping(value = "/userinfo")
public class RegisterAction {

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "register" , method = RequestMethod.POST)
    @ResponseBody
    public RetResponse register(@RequestBody String paraminfo){
        RetResponse result = new RetResponse();
        try {
            UserRegisterDTO request_info = JSON.parseObject(paraminfo, UserRegisterDTO.class);
            result = userService.register(request_info);
        }catch (Exception e) {
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg(e.getMessage());
        }
        return result;
    }

}
