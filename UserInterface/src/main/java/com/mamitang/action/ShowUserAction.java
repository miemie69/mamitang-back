package com.mamitang.action;

import com.mamitang.ReturnStatus;
import com.mamitang.entity.UserEntity;
import com.mamitang.model.UserDetail;
import com.mamitang.response.RetResponse;
import com.mamitang.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户查询Action
 * Created by lyy on 15-9-24.
 */
@RestController
@RequestMapping(value = "/userinfo")
public class ShowUserAction {

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/users/{page}/to/{numOfPage}" , method = RequestMethod.GET)
    @ResponseBody
    public RetResponse showUsers(@PathVariable("page") int page ,
                                 @PathVariable("numOfPage") int numOfPage ,
                                 @RequestParam(value = "querykey" , required = false) String querykey ,
                                 @RequestParam(value = "queryvalue" , required = false) String queryvalue ){
        RetResponse result = new RetResponse();
        try {
            result = userService.getAllUsers(page , numOfPage , querykey , queryvalue);
        }catch (Exception e){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/user/{id}" , method = RequestMethod.GET)
    @ResponseBody
    public RetResponse showUser(@PathVariable("id") int id){
        RetResponse result = new RetResponse();
        try {
            result = userService.getUserDetail(id);
        }catch (Exception e){
            result.setStatus(ReturnStatus.FAIL);
            result.setRetMsg(e.getMessage());
        }
        return result;
    }
}
