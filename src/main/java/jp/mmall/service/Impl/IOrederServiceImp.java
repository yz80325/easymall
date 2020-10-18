package jp.mmall.service.Impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jp.mmall.common.Const;
import jp.mmall.common.ResponseCode;
import jp.mmall.common.ServerResponse;
import jp.mmall.dao.*;
import jp.mmall.pojo.*;
import jp.mmall.service.IOrderService;
import jp.mmall.util.BigDecimalUtil;
import jp.mmall.util.DateTimeUtil;
import jp.mmall.util.FTPUtil;
import jp.mmall.util.PropertiesUtil;
import jp.mmall.vo.OrderItemVo;
import jp.mmall.vo.OrderProductVo;
import jp.mmall.vo.OrderVo;
import jp.mmall.vo.ShippingListVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


/**
 * Created by 2021 on 2019/10/27.
 */
@Service("iOrderService")
public class IOrederServiceImp implements IOrderService{
Logger log= LoggerFactory.getLogger(IOrederServiceImp.class);
    @Autowired
    private OrderItemMapper orderitemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    private static AlipayTradeService tradeService;

    public ServerResponse create(Long shippingId,Long userId){
        if (userId==null||shippingId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
          List<Cart> cart=cartMapper.seletCartChekcedandUserid(userId);
        //计算商品总价
        ServerResponse serverResponse=this.getCartOrderItem(userId,cart);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem>orderItems=(List<OrderItem>)serverResponse.getData();
        BigDecimal payment=this.getOrderItemTotalPrice(orderItems);
        //生成订单
        Order order=this.assembaleOrder(userId,shippingId,payment);
        if (order==null){
            return ServerResponse.createByErrorMessage("创建订单失败");
        }if (CollectionUtils.isEmpty(orderItems)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for (OrderItem orderItem:orderItems){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //批量插入
        orderitemMapper.insertBatch(orderItems);
        //减少库存
        this.reduceProductStock(orderItems);
        //清空购物车
        this.clearCart(cart);
        //返回给前端
        OrderVo orderVo=this.assembleOrderVo(order,orderItems);
        return ServerResponse.createBySuccess(orderVo);
    }
    //组装ShippingVo
    private ShippingListVo assembleShippingVo(Shipping shipping){
        ShippingListVo shippingListVo=new ShippingListVo();
        shippingListVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingListVo.setReceiverCity(shipping.getReceiverCity());
        shippingListVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingListVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingListVo.setReceiverName(shipping.getReceiverName());
        shippingListVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingListVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingListVo.setReceiverZip(shipping.getReceiverZip());
        return shippingListVo;
    }
    private OrderVo assembleOrderVo(Order order,List<OrderItem>orderItemList){
        OrderVo ordervo=new OrderVo();
        ordervo.setOrderNo(order.getOrderNo());
        ordervo.setPayment(BigDecimal.valueOf(order.getPayment()));
        order.setPaymentType(order.getPaymentType());
        ordervo.setPaymentDesc(Const.PayMent.codeof(order.getPaymentType()).getValue());
        order.setPostage(order.getPostage());
        order.setStatus(order.getStatus());
        ordervo.setStatusDesc(Const.OrderStatusEnum.codeof(order.getStatus()).getValue());
        ordervo.setShippingId(order.getShippingId());
        Shipping shipping=shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping!=null){
            ordervo.setReceiveName(shipping.getReceiverName());
            ordervo.setShippingListVos(this.assembleShippingVo(shipping));
        }
        ordervo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        ordervo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        ordervo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        ordervo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        ordervo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        ordervo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        List<OrderItemVo>orderItemVos=Lists.newArrayList();
        for (OrderItem orderItem:orderItemList){
           OrderItemVo orderItemVo=assembleOrderItem(orderItem);
            orderItemVos.add(orderItemVo);
        }
        ordervo.setOrderItemVoList(orderItemVos);
        return ordervo;
    }
    private OrderItemVo assembleOrderItem(OrderItem orderItem){
        OrderItemVo orderItemVo=new OrderItemVo();
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        return orderItemVo;
    }
    //清空购物车
    private void clearCart(List<Cart>cartList){
        for (Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }
    //减少库存
    private void reduceProductStock(List<OrderItem>orderItems){
        for (OrderItem orderItem:orderItems){
            Product product=productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }
    private Order assembaleOrder(Long userId,Long shoppingId,BigDecimal payment){
        Order order=new Order();
        order.setOrderNo(this.OrderNum());
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setUserId(userId);
        order.setPayment(payment.intValue());
        order.setPostage(0);
        order.setPaymentType(Const.PayMent.PAYMENT.getPayCode());
        order.setShippingId(shoppingId);
        int rowCount=orderMapper.insert(order);
        if (rowCount>0){
            return order;
        }
        return null;
    }
    //生成订单号
    private Long OrderNum(){
        long currentTime=System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

//计算商品的总价
    private BigDecimal getOrderItemTotalPrice(List<OrderItem>orderItems){
        BigDecimal bigDecimal=new BigDecimal("0");
        for(OrderItem orderItem:orderItems){
           bigDecimal=BigDecimalUtil.add(orderItem.getTotalPrice().doubleValue(),bigDecimal.doubleValue());
        }
      return bigDecimal;
    }

    public ServerResponse<List<OrderItem>>getCartOrderItem(Long UserId,List<Cart> carts){
        List<OrderItem>orderItems=Lists.newArrayList();
        if (CollectionUtils.isEmpty(carts)){
            return ServerResponse.createByErrorMessage("购物车没有商品");
        }
        for (Cart cart:carts){
            OrderItem orderItem1=new OrderItem();
            Product product=productMapper.selectByPrimaryKey(cart.getProductId());
            if (Const.shoppingStatus.ON_SALE.getStatus_num()!=product.getStatus()){
                return ServerResponse.createByErrorMessage("商品"+product.getName()+"不再售卖状态");
            }
            if (cart.getQuantity()>product.getStock()){
                return ServerResponse.createByErrorMessage("商品"+product.getName()+"库存不足");
            }
            orderItem1.setUserId(UserId);
            orderItem1.setProductId(product.getId());
            orderItem1.setProductImage(product.getMainImage());
            orderItem1.setProductName(product.getName());
            orderItem1.setCurrentUnitPrice(product.getPrice());
            orderItem1.setQuantity(cart.getQuantity());
            orderItem1.setTotalPrice(BigDecimalUtil.mul(cart.getQuantity(),product.getPrice()));
            orderItems.add(orderItem1);
        }
        return ServerResponse.createBySuccess(orderItems);
    }
/*
* 取消订单
* */
   public ServerResponse<String>cancel(Long userid,Long orderNo){
       Order order=orderMapper.selectByUserIdAndOrderNum(userid,orderNo);
       if (order==null){
           return ServerResponse.createByErrorMessage("此用户订单不存在");
       }
       if (order.getStatus()!=Const.OrderStatusEnum.NO_PAY.getCode()){
           return ServerResponse.createByErrorMessage("已经付款");
       }
       Order updateOrder=new Order();
       updateOrder.setId(order.getId());
       updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
       int rownum=orderMapper.updateByPrimaryKeySelective(updateOrder);
       if (rownum>0){
           return ServerResponse.createBySuccess();
       }
       return ServerResponse.createByError();
   }
    /*
    * 获取顾客选择购物车中商品名的信息
    * */
public ServerResponse getOrdderCartProduct(Long userId){
    OrderProductVo orderProductVo=new OrderProductVo();
    //从购物车中获数据
    List<Cart> cartList=cartMapper.seletCartChekcedandUserid(userId);
    ServerResponse serverResponse=this.getCartOrderItem(userId,cartList);
    if (!serverResponse.isSuccess()){
        return serverResponse;
    }
    List<OrderItem>orderItemList=(List<OrderItem>)serverResponse.getData();
    List<OrderItemVo>orderItemVoList=Lists.newArrayList();
    BigDecimal payment=new BigDecimal("0");
    for (OrderItem orderItem:orderItemList){
        payment=BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        orderItemVoList.add(assembleOrderItem(orderItem));
    }
    orderProductVo.setTotalPrice(payment);
    orderProductVo.setOrderItemVoList(orderItemVoList);
    orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
    return ServerResponse.createBySuccess(orderProductVo);
}










public ServerResponse pay(Long usrId,String path,Long orderNum){
    Map<String,String>resultMap= Maps.newHashMap();
    Order order=orderMapper.selectByUserIdAndOrderNum(usrId,orderNum);
    if (order==null){
        return ServerResponse.createByErrorMessage("用户没有该订单");
    }
    resultMap.put("orderNum",String.valueOf(order.getOrderNo()));
    // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
    // 需保证商户系统端不能重复，建议通过数据库sequence生成，
    String outTradeNo = order.getOrderNo().toString();

    // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
    String subject = new StringBuilder().append("happymmall扫码支付订单号：").append(outTradeNo).toString();

    // (必填) 订单总金额，单位为元，不能超过1亿元
    // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
    String totalAmount = order.getPayment().toString();

    // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
    // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
    String undiscountableAmount = "0";

    // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
    // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
    String sellerId = "";

    // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
    String body = new StringBuilder().append("订单").append(outTradeNo).append("共:").append(totalAmount).append("元").toString();

    // 商户操作员编号，添加此参数可以为商户操作员做销售统计
    String operatorId = "test_operator_id";

    // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
    String storeId = "test_store_id";

    // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
    ExtendParams extendParams = new ExtendParams();
    extendParams.setSysServiceProviderId("2088100200300400500");

    // 支付超时，定义为120分钟
    String timeoutExpress = "120m";

    // 商品明细列表，需填写购买商品详细信息，
    List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

    List<OrderItem>orderItemList=orderitemMapper.getByOderNoUserId(orderNum,usrId);
    for (OrderItem orderItem:orderItemList){
        GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(),orderItem.getProductName(),
                BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),
                orderItem.getQuantity().intValue());
        goodsDetailList.add(goods1);
    }

    // 创建扫码支付请求builder，设置请求参数
    AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);
    /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
     *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
     */
    Configs.init("zfbinfo.properties");

    /** 使用Configs提供的默认参数
     *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
     */
    tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
    switch (result.getTradeStatus()) {
        case SUCCESS:
            log.info("支付宝预下单成功: )");

            AlipayTradePrecreateResponse response = result.getResponse();
            dumpResponse(response);

            File folder=new File(path);
            if (!folder.exists()){
                folder.setWritable(true);
                folder.mkdirs();
            }
            // 需要修改为运行机器上的路径
            String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
            String qeFileName=String.format("qr-%s.png",response.getOutTradeNo());
            ZxingUtils.getQRCodeImge(response.getQrCode(),256,qrPath);
            File targetFile=new File(path,qeFileName);
            try {
                FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            } catch (IOException e) {
                log.error("上传二维码异常",e);
                e.printStackTrace();
            }
            log.info("qrPath:" + qrPath);
            String qrUrl=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
            resultMap.put("qrUrl",qrUrl);
            return ServerResponse.createBySuccess(resultMap);
        case FAILED:
            log.error("支付宝预下单失败!!!");
            return ServerResponse.createByErrorMessage("下单失败");
        case UNKNOWN:
            log.error("系统异常，预下单状态未知!!!");
            return ServerResponse.createByErrorMessage("系统异常");
        default:
            log.error("不支持的交易状态，交易返回异常!!!");
            return ServerResponse.createByErrorMessage("不支持的交易状态");
    }
}
    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
    public ServerResponse aliCallBack(Map<String,String>params){
        Long orderNo=Long.parseLong(params.get("out_trade_no"));
        String tradeNo=params.get("trade_no");
        String tradeStatus=params.get("trade_status");
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("非我们的订单");
        }
        if (order.getStatus()>= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccessByMessage("支付宝重复调用");
        }
        if (Const.AliPayCallBack.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo=new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayForm.ALIPAY_FORM.getPayCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }
    public ServerResponse queryPayStatu(Long userId,Long orderNo){
        Order order=orderMapper.selectByUserIdAndOrderNum(userId,orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("用户没有此订单");
        }
        if (order.getStatus()>= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
