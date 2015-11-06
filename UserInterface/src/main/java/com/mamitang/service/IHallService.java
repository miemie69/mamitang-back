package com.mamitang.service;

import com.mamitang.response.RetResponse;

import java.util.Date;

/**
 * Created by lyy on 11/2/15.
 */
public interface IHallService {

    RetResponse getHallDetail(int id);

    RetResponse getHallList(int page, int numOfPage, String querykey, String queryvalue, Date start_time, Date end_time);

    RetResponse changeHallState(int id) throws Exception;
}
