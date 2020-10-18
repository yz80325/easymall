package jp.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import jp.mmall.common.ServerResponse;
import jp.mmall.service.IProductService;
import jp.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 2021 on 2019/10/19.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Long productId){
     return iProductService.getDetail(productId);
    }
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo>list(@RequestParam(value = "keyword",required = false) String keyword,
                                        @RequestParam(value = "category",required = false) Long category,
                                        @RequestParam(value = "PageNum",defaultValue = "1") int PageNum,
                                        @RequestParam(value = "PageSize",defaultValue = "3") int PageSize,
                                        @RequestParam(value = "OrderBy",defaultValue = "")String Ordery)
    {
return iProductService.getForentProductList(keyword,category,PageNum,PageSize,Ordery);
    }
}
