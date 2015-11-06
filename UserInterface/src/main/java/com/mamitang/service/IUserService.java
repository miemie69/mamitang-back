package com.mamitang.service;

import com.mamitang.entity.UserEntity;
import com.mamitang.model.UserRegisterDTO;
import com.mamitang.response.RetResponse;

import java.util.Map;

/**
 * Created by wyp on 15-7-22.
 */
public interface IUserService {

    UserEntity login(String userName, String passWord) throws Exception;

    RetResponse register(UserRegisterDTO request_info) throws Exception;

    RetResponse getAllUsers(int num, int numOfPage, String querykey, String queryvalue);

    RetResponse getUserDetail(int id);

}
