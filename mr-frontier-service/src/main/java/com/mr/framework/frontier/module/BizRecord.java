package com.mr.framework.frontier.module;

import lombok.Data;

import java.util.Date;

@Data
public class BizRecord {

    /**
     * 所属应用
     */
    private String appName;

    /**
     * 协议类型，0为rest，1为dubbo
     */
    private Integer callType;

    /**
     * 请求方法名称(全路径)
     */
    private String methodName;

    /**
     * 请求方式(GET,POST)
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 访问者IP
     */
    private String requestIp;

    /**
     * 请求URI
     */
    private String requestUri;

    /**
     * 请求耗时
     */
    private Long timeConsuming;

    /**
     * 调用结果，0为success，1为fail
     */
    private Integer callResult;

    private Date createTime;

    private Date modifyTime;

}