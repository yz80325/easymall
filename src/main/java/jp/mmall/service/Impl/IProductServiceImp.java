package jp.mmall.service.Impl;

import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import jp.mmall.common.Const;
import jp.mmall.common.ResponseCode;
import jp.mmall.common.ServerResponse;
import jp.mmall.dao.CategoryMapper;
import jp.mmall.dao.ProductMapper;
import jp.mmall.pojo.Category;
import jp.mmall.pojo.Product;
import jp.mmall.service.ICategoryService;
import jp.mmall.service.IProductService;
import jp.mmall.util.DateTimeUtil;
import jp.mmall.util.PropertiesUtil;
import jp.mmall.vo.ProductDetailVo;
import jp.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2021 on 2019/10/17.
 */
@Service("iProductServiceImp")
public class IProductServiceImp implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService categoryService;
    public ServerResponse saveOrUpdateProduct(Product product){
        if (product!=null)
        {
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[]subImageArray=product.getSubImages().split(",");
                if (subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if (product.getId()!=null){
               int count=productMapper.updateByPrimaryKey(product);
                if (count>0){
                    return ServerResponse.createBySuccessByMessage("更新产品成功");
                }else{
                    return ServerResponse.createBySuccessByMessage("更新产品失败");
                }
            }else{
              int count=productMapper.insert(product);
                if(count>0){
                    return ServerResponse.createBySuccessByMessage("新增产品成功");
                }else{
                    return ServerResponse.createByErrorMessage("新增产品失败");
                }

            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品不正确");
    }

    public ServerResponse<String> setSaleStatus(Long productid,Integer status){
        if (productid==null||status==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productid);
        product.setStatus(status);
        int rowCount=productMapper.updateByPrimaryKeySelective(product);
        if (rowCount>0){
            return ServerResponse.createBySuccessByMessage("更改商品状态成功");
        }
        return ServerResponse.createByErrorMessage("更改商品状态失败");
    }

    public ServerResponse<ProductDetailVo>getSaleDetails(Long productid){
        if (productid==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productid);
        if (product==null){
            return ServerResponse.createByErrorMessage("商品已经下架");
        }
        ProductDetailVo productDetailVo=admittProductDetail(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }
    //DTOへ移動

    public ServerResponse<PageInfo> getProductList(int PageNum,int PageSize){
        PageHelper.startPage(PageNum,PageSize);
        List<Product>productList=productMapper.getList();
        List<ProductListVo>productListVos=new ArrayList<>();
        for (Product productitem:productList){
            ProductListVo productListVo=productListVo(productitem);
            productListVos.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    //根据检索来分页
    public ServerResponse<PageInfo>PageofSearch(String ProductName,Long productId,int PageNum,int PageSize){
        PageHelper.startPage(PageNum,PageSize);
        if (StringUtils.isNotEmpty(ProductName)){
           ProductName =new StringBuilder("%").append(ProductName).append("%").toString();
        }
       List<Product>productList=productMapper.selectProductNameandId(ProductName,productId);
        List<ProductListVo>productListVos=new ArrayList<>();
        for (Product productitem:productList){
            ProductListVo productListVo=productListVo(productitem);
            productListVos.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<ProductDetailVo> getDetail(Long productID){
        if (productID==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productID);
        if (product==null){
            return ServerResponse.createByErrorMessage("商品已下架");
        }
        if (product.getStatus()!=Const.shoppingStatus.ON_SALE.getStatus_num()){
            return ServerResponse.createByErrorMessage("商品已下架");
        }
        ProductDetailVo productDetailVo=admittProductDetail(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo>getForentProductList(String keyword,Long categoryId,int PageNum,int PageSize,String orderBy){
        if (StringUtils.isBlank(keyword)&&categoryId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //接收种类下的子种类
        List<Long>categoryIdList= new ArrayList<Long>();
        if (categoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            if (category==null&&StringUtils.isBlank(keyword)){
                //返回一个空页
                PageHelper.startPage(PageNum,PageSize);
                List<ProductListVo>productListVos= Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListVos);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList=categoryService.selectCategoryAndChildrenId(categoryId).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
           keyword=new StringBuilder("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(PageNum,PageSize);
        //排序
        if (StringUtils.isNotBlank(orderBy)){
          if(Const.OrderBy.PRICE_ASC_DESC.contains(orderBy)){
              String[]orderyArry=orderBy.split("_");
              PageHelper.orderBy(orderyArry[0]+" "+orderyArry[1]);
          }
        }
        List<Product>productList=productMapper.selectProductKeyandCateId(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        if (productList==null){
            return ServerResponse.createBySuccessByMessage("获取商品列表失败");
        }
        List<ProductListVo>productListVos=new ArrayList<>();
        for (Product productitem:productList){
            ProductListVo productListVo=productListVo(productitem);
            productListVos.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVos);
       return ServerResponse.createBySuccess(pageInfo);
    }


    //传到前端的Product
    public ProductDetailVo admittProductDetail(Product product){
     ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setPrice(BigDecimal.valueOf(product.getPrice()));
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setStock(product.getStock());
        //imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        //ParentId
        Category category=categoryMapper.selectByPrimaryKey(product.getId());
        if (category==null){
            productDetailVo.setParentCategoryId((long) 0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        //date的转换
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        return productDetailVo;
    }
    //分页的Product
    public ProductListVo productListVo(Product product){
      ProductListVo productListVo=new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setName(product.getName());
        productListVo.setStatus(product.getStatus());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(BigDecimal.valueOf(product.getPrice()));
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        return productListVo;
    }


}
