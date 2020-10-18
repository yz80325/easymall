package jp.mmall.service;

import com.github.pagehelper.PageInfo;
import jp.mmall.common.ServerResponse;
import jp.mmall.pojo.Shipping;

/**
 * Created by 2021 on 2019/10/26.
 */
public interface IShippingService {
    ServerResponse add(Long userId, Shipping shipping);
    //删除
    ServerResponse<String> del(Long userId,Long ShippingId);
    //更新
    ServerResponse<String> update(Long userId,Shipping shipping);
    ServerResponse<Shipping> select(Long userId,Long shippingId);
    ServerResponse<PageInfo>list(Long userId, int PageNum, int PageSize);
}
