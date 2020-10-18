package jp.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.sun.deploy.net.HttpResponse;
import jp.mmall.common.Const;
import jp.mmall.common.ResponseCode;
import jp.mmall.common.ServerResponse;
import jp.mmall.pojo.Product;
import jp.mmall.pojo.User;
import jp.mmall.service.IFileService;
import jp.mmall.service.IProductService;
import jp.mmall.service.IUserService;
import jp.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 2021 on 2019/10/17.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;
//更新或者插入
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productService(HttpSession session, Product product){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdminRoler(user).isSuccess()){
           return iProductService.saveOrUpdateProduct(product);

        }else{
            return ServerResponse.createByErrorMessage("你不是管理员没有访问权限");
        }

    }

    //更新产品状态
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session,Long productid,Integer status){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdminRoler(user).isSuccess()){
            // 2019/10/17 增加产品的业务逻辑
            return iProductService.setSaleStatus(productid,status);

        }else{
            return ServerResponse.createByErrorMessage("你不是管理员没有访问权限");
        }

    }
    //获取商品详细信息
    @RequestMapping("details.do")
    @ResponseBody
    public ServerResponse getSaleDetails(HttpSession session,Long productid){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdminRoler(user).isSuccess()){
            // 2019/10/17 增加产品的业务逻辑
            return iProductService.getSaleDetails(productid);



        }else{
            return ServerResponse.createByErrorMessage("你不是管理员没有访问权限");
        }

    }

    //进行分页
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getlists(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "3")int pageSize){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdminRoler(user).isSuccess()){
            // 2019/10/17 获取分页列表
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("你不是管理员没有访问权限");
        }

    }
    //検索とページを分ける
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse PageforSearch(HttpSession session, String productName,Long productId,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "3")int pageSize){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdminRoler(user).isSuccess()){

             return iProductService.PageofSearch(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("你不是管理员没有访问权限");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
      User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdminRoler(user).isSuccess()){
            //获取服务器下的地址
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap= Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
    }else{
            return ServerResponse.createByErrorMessage("你不是管理员没有访问权限");
        }
    }

    @RequestMapping("richtext_img_text.do")
    @ResponseBody
    public Map richtext_img(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap=Maps.newHashMap();
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
             resultMap.put("success",false);
             resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        if (iUserService.checkAdminRoler(user).isSuccess()){
            //获取服务器下的地址
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            if (StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","你不是管理员没有访问权限");
            return resultMap;
        }
    }
}
