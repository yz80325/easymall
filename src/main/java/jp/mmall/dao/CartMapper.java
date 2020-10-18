package jp.mmall.dao;

import jp.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    //根据客户id和商品id来查询此购物出
    Cart selectCustIdandShoppingId(@Param("custid")Long custid,@Param("productId")Long productid);
    //根据客户id来查询购物车
    List<Cart> selectCartByUserId(Long UserId);
    //查询点击数
    int selectCartCgeckedByUserId(Long userid);

    //删除购物车的所选商品
    int deleteByUserIdProdyctIds(@Param("custid")Long userid,@Param("productId")List<String> productidList);
    //全选你和不全选
    int checkedUserAllChecked(@Param("userid") Long userId,@Param("productId")Long productid,@Param("checked") short checked);
    //数量
    int selectCartProductCount(@Param("userId")Long userId);
    //购物车选择的
    List<Cart> seletCartChekcedandUserid(Long userId);
}