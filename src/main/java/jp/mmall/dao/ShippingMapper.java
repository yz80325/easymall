package jp.mmall.dao;

import jp.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByShippingIdUserId(@Param("userId")Long userId,@Param("shippingId")Long shippingId);

    int updateByShinppingandByUserId(Shipping record);

    Shipping selectShippingByUserIdandShId(@Param("userId")Long userId,@Param("shippingId")Long shippingId);

    List<Shipping> selectBYUserId(@Param("userId") Long userId);
}