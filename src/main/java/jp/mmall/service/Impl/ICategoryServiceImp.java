package jp.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jp.mmall.common.ServerResponse;
import jp.mmall.dao.CategoryMapper;
import jp.mmall.pojo.Category;
import jp.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


/**
 * Created by 2021 on 2019/10/16.
 */
@Service("iCategoryService")
public class ICategoryServiceImp implements ICategoryService{

    private Logger logger=LoggerFactory.getLogger(ICategoryServiceImp.class);
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public ServerResponse addCategory(String categoryName, Long parentId) {
        if (parentId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus((short) 1);
        int rowCount=categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessByMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    @Override
    public ServerResponse updateCategoryName(Long categoryId, String categoryName) {
        if (categoryId==null||StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowNumber=categoryMapper.updateByPrimaryKeySelective(category);
        if (rowNumber>0){
            return ServerResponse.createBySuccessByMessage("更新品类成功");
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    public ServerResponse<List<Category>>getChildParallelCategory(Long categoryId){
     List<Category>categoryList=categoryMapper.selectChildCategoryByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
           logger.info("为获取任何子元素");
        }
        return ServerResponse.createBySuccess(categoryList);
    }
/*
* 递归查询本节点的Id和递归节点的Id
* */
    public ServerResponse selectCategoryAndChildrenId(Long categoryId){
    Set<Category>categorySet= Sets.newHashSet();
        findChildCategory(categorySet,categoryId);

        List<Long>categoryIdList= Lists.newArrayList();
        if (categoryId!=null){
            for (Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }


//递归算法算子节点,查找父类下的子类
    private Set<Category>findChildCategory(Set<Category>categorySet,Long categoryId){
    Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有个退出条件,查找子类
   List<Category>categoryList=categoryMapper.selectChildCategoryByParentId(categoryId);
  for (Category categoryitem:categoryList){
      findChildCategory(categorySet,categoryitem.getId());
  }
        return categorySet;
    }

}
