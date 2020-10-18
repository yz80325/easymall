package jp.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import jp.mmall.common.ServerResponse;
import jp.mmall.dao.ShippingMapper;
import jp.mmall.pojo.Shipping;
import jp.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 2021 on 2019/10/26.
 */
@Service("iShippingService")
public class IShippingImp implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;
    //添加地址
    public ServerResponse add(Long userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount=shippingMapper.insert(shipping);
        if (rowCount>0){
            Map result= Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }
    //删除地址
    public ServerResponse<String> del(Long userId,Long ShippingId){
        int resultCount=shippingMapper.deleteByShippingIdUserId(userId,ShippingId);
        if(resultCount>0){
            return ServerResponse.createBySuccessByMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }
    //更新地址
    public ServerResponse<String> update(Long userId,Shipping shipping){
        shipping.setUserId(userId);
        int rowCount=shippingMapper.updateByShinppingandByUserId(shipping);
        if (rowCount>0){
            return ServerResponse.createBySuccessByMessage("更新成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    //查询
    public ServerResponse<Shipping> select(Long userId,Long shippingId){
        Shipping shipping=shippingMapper.selectShippingByUserIdandShId(userId,shippingId);
        if (shipping==null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess("查询地址成功",shipping);
    }
    //分页
    public ServerResponse<PageInfo>list(Long userId,int PageNum,int PageSize){
        PageHelper.startPage(PageNum,PageSize);
        List<Shipping>shippingList=shippingMapper.selectBYUserId(userId);
        PageInfo pageInfo=new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
