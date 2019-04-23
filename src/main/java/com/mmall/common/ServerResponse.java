package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * java bean 其中这个泛型为User时这个bean同样可以拥有User这个bean的属性
 * @param <T> 泛型可以为任何类型
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化Json的时候，如果是null的对象，key也会消失
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status){
        this.status=status;
    }
    private ServerResponse(int status, T data){
        this.status=status;
        this.data=data;
    }
    private ServerResponse(int status, T data, String msg){
        this.status=status;
        this.data=data;
        this.msg=msg;
    }
    private ServerResponse(int status, String msg){
        this.status=status;
        this.msg=msg;
    }
    @JsonIgnore
    //使之不在json序列化结果当中
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }
    public  T getData(){
        return data;
    }
    public String getMsg(){
        return msg;
    }
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data, msg);
    }
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return  new ServerResponse<T>(errorCode,errorMessage);
    }



}
