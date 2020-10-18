package jp.mmall.service.Impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import jp.mmall.common.Const;
import jp.mmall.common.ResponseCode;
import jp.mmall.common.ServerResponse;
import jp.mmall.dao.CartMapper;
import jp.mmall.dao.ProductMapper;
import jp.mmall.pojo.Cart;
import jp.mmall.pojo.Category;
import jp.mmall.pojo.Product;
import jp.mmall.service.ICartService;
import jp.mmall.util.BigDecimalUtil;
import jp.mmall.util.PropertiesUtil;
import jp.mmall.vo.CartProductVo;
import jp.mmall.vo.CartVo;
import jp.mmall.vo.ProductListVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2021 on 2019/10/20.
 */
@Service
public class ICartServiceImp implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
//添加到购物车

    public ServerResponse<CartVo> add(Long customerId, Long productId, Long count) {
        if (productId==null||count==null){

            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("没有此商品！");
        }
        Cart cart = cartMapper.selectCustIdandShoppingId(customerId, productId);
        if (cart == null) {
            Cart cartitem = new Cart();
            cartitem.setQuantity(count);
            cartitem.setChecked((long) Const.ProductCheck.isChecked);
            cartitem.setProductId(productId);
            cartitem.setUserId(customerId);

            cartMapper.insert(cartitem);
        } else {
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);

        }
        return this.list(customerId);
    }
//更新购物车
    public ServerResponse<CartVo> update(Long userid,Long productid,Long count){
        if (productid==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCustIdandShoppingId(userid,productid);
        if (cart!=null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userid);
    }

    //删除购物车
    public ServerResponse<CartVo>deleteProduct(Long userid,String productIds){
        List<String>productlist= Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productlist)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProdyctIds(userid,productlist);
        return this.list(userid);
    }

    //获取信息
    public ServerResponse<CartVo>list(Long userid){
        CartVo cartVo=this.getCartVo(userid);
        return ServerResponse.createBySuccess(cartVo);
    }
    //全选
    public ServerResponse<CartVo>selectOrSelectAll(Long userid,Long productId,short checked){
        cartMapper.checkedUserAllChecked(userid,productId,checked);
        return this.list(userid);
    }
    //获得购物车总数
    public ServerResponse<Integer>getCartProductCount(Long userId){
        if (userId==null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }
    private CartVo getCartVo(Long userId) {
        CartVo cartvo= new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> CartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartitem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setUserId(cartitem.getUserId());
                cartProductVo.setId(cartitem.getId());
                cartProductVo.setProductId(cartitem.getProductId());
                Product product = productMapper.selectByPrimaryKey(cartitem.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainInage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setSubTitle(product.getSubtitle());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductStauts(product.getStatus());
                    cartProductVo.setProductPrice(BigDecimal.valueOf(product.getPrice()));
                    //判断库存
                    Long cartCount = 0L;
                    if (product.getStock() >= cartitem.getQuantity()) {
                        cartCount = cartitem.getQuantity();
                        cartProductVo.setLimitProduct(Const.Cart.CARTLIMIT_SUCCESS);
                    } else {
                        cartCount = product.getStock();
                        cartProductVo.setLimitProduct(Const.Cart.CARTLIMIT_FAILURE);
                        //更新库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartitem.getId());
                        cartForQuantity.setQuantity(cartCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(cartCount.intValue());
                    //某一个的总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChexced(cartitem.getChecked().intValue());
                }
                if (cartitem.getChecked()==Const.ProductCheck.isChecked){
                    //如果被勾选加到总价
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                CartProductVoList.add(cartProductVo);
            }
        }
        cartvo.setCartTotalPrice(cartTotalPrice);
        cartvo.setCartProductVoList(CartProductVoList);
        cartvo.setAllChecked(this.getAllCheckedStatus(userId));
        cartvo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartvo;
    }
    //判断是否全选
    private boolean getAllCheckedStatus(Long userid){
        if (userid==null){
            return false;
        }
        return cartMapper.selectCartCgeckedByUserId(userid)==0;
    }
}
