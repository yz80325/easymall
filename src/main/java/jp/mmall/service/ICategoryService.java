package jp.mmall.service;

import jp.mmall.common.ServerResponse;
import jp.mmall.pojo.Category;

import java.util.List;

/**
 * Created by 2021 on 2019/10/16.
 */
public interface ICategoryService {
    //添加种类
    ServerResponse addCategory(String category,Long parentId);
    //更新种类
    ServerResponse updateCategoryName(Long categoryId,String categoryName);
    //获取子类信息
    ServerResponse<List<Category>>getChildParallelCategory(Long categoryId);
    //递归查询信息
    ServerResponse<List<Long>> selectCategoryAndChildrenId(Long categoryId);
}
