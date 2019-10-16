package jp.mmall.controller.backend;

import jp.mmall.common.Const;
import jp.mmall.common.ServerResponse;
import jp.mmall.pojo.User;
import jp.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 2021 on 2019/10/15.
 */
@Controller
@RequestMapping("/manage/user")
public class UserMangerController {
    @Autowired
    private IUserService iUserService;


    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>login(String username, String password, HttpSession session){
        ServerResponse<User>response=iUserService.login(username,password);
        if (response.isSuccess()){
            User user=response.getData();
            if (user.getRole()== Const.Role.ROLE_ADMIN){
                //说明是管理员
                session.setAttribute(Const.CURRENT_USER,user);
            }else {
                return ServerResponse.createByErrorMessage("你不是管理员无法进行登录");
            }
        }
        return response;
    }
}
