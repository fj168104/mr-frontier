package com.mr.framework.frontier.module;

import lombok.Data;

import java.util.Date;

@Data
public class BizConfig {

    /**
     * 所属应用
     */
    private String appName;

    /**
     * 所属应用描述
     */
    private String appNameDesp;

    /**
     * 目标URL
     */
    private String url;

    /**
     * 账号是否锁定，1：锁定，0未锁定
     */
    private Boolean isLock;

    private Date createTime;

    private Date modifyTime;

}