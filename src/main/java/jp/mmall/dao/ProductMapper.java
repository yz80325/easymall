package jp.mmall.dao;

import jp.mmall.pojo.Category;
import jp.mmall.pojo.Product;
import jp.mmall.vo.ProductListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    //返回一个集合
    List<Product> getList();
    //根据查询条件去查询
    List<Product>selectProductNameandId(@Param("ProductName")String ProductName,@Param("ProductId")Long ProductId);
    //根据前台的数据去查询
    List<Product>selectProductKeyandCateId(@Param("keyWord")String keyWord,@Param("CategoryId") List<Long> CategoryId);
}