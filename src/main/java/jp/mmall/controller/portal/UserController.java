package jp.mmall.controller.portal;

import jp.mmall.common.Const;
import jp.mmall.common.ResponseCode;
import jp.mmall.common.ServerResponse;
import jp.mmall.pojo.User;
import jp.mmall.service.IUserService;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 2021 on 2019/10/14.
 */
@Controller
@RequestMapping("/user/")
public class UserController {


    @Autowired
    private IUserService iUserService;
    /*用户登录*/
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
public ServerResponse<User> login(String username, String password, HttpSession session){
        //service-->mybatis-->dao
       ServerResponse<User> response=iUserService.login(username,password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
}
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> loginout(HttpSession session){
      session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkedValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>getUserInfo(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取信息");
    }

    //获取问题
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>forgetGetQuestion(String username){
       return iUserService.selectofQuesion(username);
    }
    //找回密码,回答找回密码问题
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>forgetCheckAnswer(String username,String question,String answer){
     return iUserService.checkAnswer(username,question,answer);
    }


    //重置密码
    @RequestMapping(value = "forget_rest_password.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String>forgetRestPassword(String username,String passwordNew,String forgetToken){
      return iUserService.forgetRestPassword(username,passwordNew,forgetToken);
    }

    //登录状态重置密码
    @RequestMapping(value = "rest_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>resetPassword(HttpSession session,String passwrdOld,String passwordNew){
      //获取用户
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.restPassword(user,passwrdOld,passwordNew);
    }

    //更新用户信息
    @RequestMapping(value = "update_Information.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<User>updateInformation(HttpSession session,User user){
        User CurrentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if (CurrentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(CurrentUser.getId());
        user.setUsername(CurrentUser.getUsername());
       ServerResponse<User>response=iUserService.updateInformation(user);
        if (response.isSuccess()){
            response.getData().setUsername(CurrentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    //获取用户详细信息
    @RequestMapping(value = "get_Information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>get_information(HttpSession session){
        //如果未登录的情况系需要强制登录
        User CurrentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if (CurrentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录！需要强制登录");
        }
        return iUserService.getInformation(CurrentUser.getId());
    }
}
