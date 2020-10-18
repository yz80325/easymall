package jp.mmall.service;

import com.github.pagehelper.PageInfo;
import jp.mmall.common.ServerResponse;
import jp.mmall.pojo.Product;
import jp.mmall.vo.ProductDetailVo;

/**
 * Created by 2021 on 2019/10/17.
 */
public interface IProductService {

    /*
    * 更新产品或者插入新产品
    * */
    ServerResponse saveOrUpdateProduct(Product product);
    //修改商品的贩卖属性
    ServerResponse<String> setSaleStatus(Long productid,Integer status);
    //获取信息
    ServerResponse<ProductDetailVo>getSaleDetails(Long productid);
    //获取分页
    ServerResponse<PageInfo> getProductList(int PageNum, int PageSize);
    //根据检索分页
    ServerResponse<PageInfo>PageofSearch(String ProductName,Long productId,int PageNum,int PageSize);
    //前端所查詢到的細節
    ServerResponse<ProductDetailVo> getDetail(Long productID);
    //前端的分页
    ServerResponse<PageInfo>getForentProductList(String keyword,Long categoryId,int PageNum,int PageSize,String orderBy);
}
