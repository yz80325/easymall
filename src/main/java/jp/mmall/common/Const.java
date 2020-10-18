package jp.mmall.common;

import com.google.common.collect.Sets;
import com.sun.xml.internal.ws.api.message.Packet;

import java.util.Set;

/**
 * Created by 2021 on 2019/10/14.
 * 常量类
 */

public class Const {
    public static final String CURRENT_USER="currentUser";
    public static final String USERNAME="username";
    public static final String EMAIL="email";

   //排序
    public interface OrderBy{
       //用Set处理效率比较高
       Set<String>PRICE_ASC_DESC= Sets.newHashSet("price_asc","price_desc");
   }
    //购物车里的数量
    public interface Cart{
       String CARTLIMIT_SUCCESS="CARTLIMIT_SUCCESS";
        String CARTLIMIT_FAILURE="CARTLIMIT_FAILURE";
    }

    //角色
    public interface Role{
        short ROLE_CUSTOMER=0;//消费者
        short ROLE_ADMIN=1;//管理员
    }
    //商品的状态
    public enum shoppingStatus{
        ON_SALE(1,"在线");
        private int Status_num;
        private String Status;
        shoppingStatus(int Status_num,String Status){
            this.Status_num=Status_num;
            this.Status=Status;
        }
        public String getStatus(){
            return Status;
        }
        public int getStatus_num(){
            return Status_num;
        }
    }
    //商品的选中状态
    public interface ProductCheck{
        short isChecked=1;
        short isNotChecked=0;
    }
    //订单的状态
    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已支付"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭")
        ;
        OrderStatusEnum(int Code,String value){
            this.Code=Code;
            this.value=value;
        }
        private String value;
        private int Code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return Code;
        }
        public static OrderStatusEnum codeof(int code){
            for(OrderStatusEnum orderStatusEnum:values()){
                if (orderStatusEnum.getCode()==code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }
    public interface  AliPayCallBack{
        String TRADE_STATUS_WAIT_BUYER_PAY="WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS="TRADE_SUCCESS";
        String  RESPONSE_SUCCESS="success";
        String RESPONSE_FAILURE="failed";
    }
    public enum PayForm{
        ALIPAY_FORM(1,"支付宝")
        ;
        PayForm(int PayCode,String value){
            this.PayCode=PayCode;
            this.value=value;
        }
        private String value;
        private int PayCode;

        public String getValue() {
            return value;
        }

        public int getPayCode() {
            return PayCode;
        }
    }
    public enum PayMent{
        PAYMENT(1,"网络支付");
        PayMent(int PayCode,String value){
            this.PayCode=PayCode;
            this.value=value;
        }
        private String value;
        private int PayCode;

        public String getValue() {
            return value;
        }

        public int getPayCode() {
            return PayCode;
        }
        //通过int来找
        public static PayMent codeof(int code){
            for(PayMent payMent:values()){
                if (payMent.getPayCode()==code){
                    return payMent;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
}
