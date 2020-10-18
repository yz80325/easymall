package jp.mmall.vo;

import java.math.BigDecimal;

/**
 * Created by 2021 on 2019/10/21.
 * 点开购物车后所出现的页面
 */
public class CartProductVo {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;//购物车中商品的数量
    private String productName;
    private String SubTitle;
    private String productMainInage;
    private BigDecimal productPrice;//商品单价
    private Integer productStauts;//商品状态
    private BigDecimal productTotalPrice;//商品总价
    private Long productStock;
    private Integer productChexced;//是否被选中

    private String limitProduct;//限制数量一个返回结果

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductStauts() {
        return productStauts;
    }

    public void setProductStauts(Integer productStauts) {
        this.productStauts = productStauts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSubTitle() {
        return SubTitle;
    }

    public void setSubTitle(String subTitle) {
        SubTitle = subTitle;
    }

    public String getProductMainInage() {
        return productMainInage;
    }

    public void setProductMainInage(String productMainInage) {
        this.productMainInage = productMainInage;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public Long getProductStock() {
        return productStock;
    }

    public void setProductStock(Long productStock) {
        this.productStock = productStock;
    }

    public Integer getProductChexced() {
        return productChexced;
    }

    public void setProductChexced(Integer productChexced) {
        this.productChexced = productChexced;
    }

    public String getLimitProduct() {
        return limitProduct;
    }

    public void setLimitProduct(String limitProduct) {
        this.limitProduct = limitProduct;
    }
}
