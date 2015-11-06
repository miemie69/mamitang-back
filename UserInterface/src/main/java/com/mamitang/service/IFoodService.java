package com.mamitang.service;

import com.mamitang.request.FoodUpdateRequest;
import com.mamitang.response.RetResponse;

import java.util.Date;

/**
 * Created by lyy on 10/28/15.
 */
public interface IFoodService {

    RetResponse getFoodDetail(int id);

    RetResponse getFoodList(int page, int numOfPage, String querykey, String queryvalue , Date start_time, Date end_time);

    RetResponse updateFood(int id, FoodUpdateRequest request_info);
}
