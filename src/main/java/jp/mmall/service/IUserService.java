package jp.mmall.service;

import jp.mmall.common.ServerResponse;
import jp.mmall.pojo.User;
import org.omg.CORBA.Object;

import javax.servlet.http.HttpSession;

/**
 * Created by 2021 on 2019/10/14.
 */
public interface IUserService {
   //登陆
   ServerResponse<User> login(String username, String password);
   //注册
   ServerResponse<String> register(User user);
  //数据库里进行检测然后反馈到前端
   ServerResponse<String>checkValid(String str,String type);
  //查找问题
    ServerResponse<String> selectofQuesion(String username);
    //通过问题找回密码
    ServerResponse<String>checkAnswer(String username,String question,String answer);
    //重置密码
    ServerResponse<String>forgetRestPassword(String username,String passwordNew,String forgetToken);
    //登录状态下重置密码
    ServerResponse<String>restPassword(User user, String passwordOld, String passwordNew);
    //更新用户信息
    ServerResponse<User>updateInformation(User user);
    //获取用户的所有信息
    ServerResponse<User>getInformation(Long userId);
    //判断是否为管理层
    ServerResponse checkAdminRoler(User user);
}
