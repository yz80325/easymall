package jp.mmall.controller.backend;

import jp.mmall.common.Const;
import jp.mmall.common.ResponseCode;
import jp.mmall.common.ServerResponse;
import jp.mmall.pojo.User;
import jp.mmall.service.ICategoryService;
import jp.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 2021 on 2019/10/16.
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryname,@RequestParam(value="parentId",defaultValue = "0") long parentid){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否为管理员,根据MVC思想写在Service层
      if(iUserService.checkAdminRoler(user).isSuccess()){
      //是管理员
      // TODO: 2019/10/16 增加分类的处理
      return iCategoryService.addCategory(categoryname,parentid);
      }else{
          return ServerResponse.createByErrorMessage("您不是管理员，需要权限");
      }
    }


    @RequestMapping("set_category_name.do")
    @ResponseBody
    public  ServerResponse setCategoryName(HttpSession session,Long categoryId,String categoryName){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(iUserService.checkAdminRoler(user).isSuccess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("您不是管理员，需要权限");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public  ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Long categoryId){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(iUserService.checkAdminRoler(user).isSuccess()){
            return iCategoryService.getChildParallelCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("您不是管理员，需要权限");
        }
    }


    @RequestMapping("get_category.do")
    @ResponseBody
    public  ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0")Long categoryId){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(iUserService.checkAdminRoler(user).isSuccess()){
           //查询子节点的Id以及递归子节点的Id
        return iCategoryService.selectCategoryAndChildrenId(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("您不是管理员，需要权限");
        }
    }
}
