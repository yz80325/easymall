package jp.mmall.service;

import jp.mmall.common.ServerResponse;
import jp.mmall.vo.CartVo;

import javax.servlet.http.HttpSession;

/**
 * Created by 2021 on 2019/10/20.
 */
public interface ICartService {
    ServerResponse<CartVo> add(Long customerId, Long productId, Long count);
    //更新购物车
    ServerResponse<CartVo> update(Long userid,Long productid,Long count);
    //删除购物车中的产品
    ServerResponse<CartVo>deleteProduct(Long userid,String productIds);
    ServerResponse<CartVo>list(Long userid);
    //全选
    ServerResponse<CartVo>selectOrSelectAll(Long userid,Long productId,short checked);
    ServerResponse<Integer>getCartProductCount(Long userId);
}
