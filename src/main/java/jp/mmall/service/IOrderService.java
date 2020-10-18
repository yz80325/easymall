package jp.mmall.service;

import jp.mmall.common.ServerResponse;

import java.util.Map;

/**
 * Created by 2021 on 2019/10/27.
 */
public interface IOrderService {
    ServerResponse pay(Long usrId, String path, Long orderNum);
    ServerResponse aliCallBack(Map<String,String> params);
    ServerResponse queryPayStatu(Long userId,Long orderNo);
    ServerResponse create(Long shippingId,Long userId);
    ServerResponse<String>cancel(Long userid,Long orderNo);
    ServerResponse getOrdderCartProduct(Long userId);
}
