package com.jk.modules.biz.model;

import com.jk.common.base.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class BizConfig extends BaseEntity {

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

}