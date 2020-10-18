package jp.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 2021 on 2019/11/17.
 */
public class OrderProductVo {
    private List<OrderItemVo>orderItemVoList;
    private BigDecimal totalPrice;
    private String ImageHost;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getImageHost() {
        return ImageHost;
    }

    public void setImageHost(String imageHost) {
        ImageHost = imageHost;
    }
}
