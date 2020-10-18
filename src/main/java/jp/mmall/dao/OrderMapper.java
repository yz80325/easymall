package jp.mmall.dao;

import jp.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNum(@Param("userId")Long userId,@Param("OrderNum")Long OrderNum);

    Order selectByOrderNo(Long orderNo);
}