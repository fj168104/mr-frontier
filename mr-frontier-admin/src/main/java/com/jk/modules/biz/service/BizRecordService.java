package com.jk.modules.biz.service;

import com.github.pagehelper.PageInfo;
import com.jk.common.base.service.BaseService;
import com.jk.modules.biz.model.BizRecord;

/**
 * @author fengj
 * Created by JK on 2018/3/2.
 */
public interface BizRecordService extends BaseService<BizRecord> {

    /**
     * 分页查询调用记录列表
     * @param pageNum
     * @param pageSize
     * @param appName
     * @param startTime
     * @param endTime
     * @return
     */
    PageInfo<BizRecord> findPage(Integer pageNum, Integer pageSize, String appName, String startTime, String endTime);
}
