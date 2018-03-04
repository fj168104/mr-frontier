package com.mr.framework.frontier.service;

import com.mr.framework.frontier.module.BizConfig;
import com.mr.framework.frontier.module.BizRecord;

/**
 * Created by fengj on 2018/3/5.
 */
public interface FilterService {

    boolean saveBizRecord(BizRecord bizRecord);

    BizConfig findBizConfig(String appName);
}
