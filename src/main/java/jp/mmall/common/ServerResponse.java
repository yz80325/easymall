package jp.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import sun.plugin2.message.Message;

import java.io.Serializable;

/**
 * Created by 2021 on 2019/10/14.
 */
//在其他值为空的时候只传有值的
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证Json的序列化同时，null时key也会消失
public class ServerResponse<T>implements Serializable {
    private int status;
    private String msg;
    private T data;
    private ServerResponse(int status){
        this.status=status;
    }
    private  ServerResponse(int status,T data){
        this.status=status;
        this.data=data;
    }
    private  ServerResponse(int status,String msg,T data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }
    private  ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }
    //只是判断不需要形成json
    @JsonIgnore
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }
    public int getStatus(){
        return status;
    }
    public T getData(){
        return data;
    }
    public String getMsg(){
        return msg;
    }
    /*
    * 必须在方法前加<T>，否则就不是泛型方法
    * */
    public static <T>ServerResponse<T>createBySuccess(){
      return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T>ServerResponse<T>createBySuccessByMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static <T>ServerResponse<T>createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T>ServerResponse<T>createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    public static <T>ServerResponse<T>createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public static <T>ServerResponse<T>createByErrorMessage(String errorMessage){
      return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errorMessage);
    }
    /*
    * code的动态生成方法
    * */
    public static <T> ServerResponse<T> createByErrorCodeMessage(int code,String msg){
        return new ServerResponse<T>(code,msg);
    }
}
