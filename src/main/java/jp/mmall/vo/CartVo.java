package jp.mmall.vo;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 2021 on 2019/10/21.
 */
public class CartVo {
    private List<CartProductVo>cartProductVoList;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked;
    private String ImageHost;//跟目录

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return ImageHost;
    }

    public void setImageHost(String imageHost) {
        ImageHost = imageHost;
    }
}
