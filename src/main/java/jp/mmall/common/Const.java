package jp.mmall.common;

/**
 * Created by 2021 on 2019/10/14.
 * 常量类
 */

public class Const {
    public static final String CURRENT_USER="currentUser";
    public static final String USERNAME="username";
    public static final String EMAIL="email";
    public interface Role{
        short ROLE_CUSTOMER=0;//消费者
        short ROLE_ADMIN=1;//管理员
    }
}
