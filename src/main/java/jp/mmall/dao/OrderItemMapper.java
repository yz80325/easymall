package jp.mmall.dao;

import jp.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
//查询用户的订单列表
    List<OrderItem>getByOderNoUserId(@Param("orderNo")Long orderNo,@Param("userId")Long userId);
    //批量插入数据库
    void insertBatch(@Param("orderItems") List<OrderItem>orderItems);
}