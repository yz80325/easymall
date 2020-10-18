package jp.mmall.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 2021 on 2019/10/31.
 */
//订单明细
public class OrderItemVo {
    private Long orderNo;

    private Long productId;

    private String productName;

    private String productImage;

    private Integer currentUnitPrice;

    private Long quantity;

    private BigDecimal totalPrice;

    private String createTime;

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Integer getCurrentUnitPrice() {
        return currentUnitPrice;
    }

    public void setCurrentUnitPrice(Integer currentUnitPrice) {
        this.currentUnitPrice = currentUnitPrice;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
