package com.jk.modules.biz.mapper;

import com.jk.common.base.mapper.BaseMapper;
import com.jk.modules.biz.model.BizConfig;

public interface BizConfigMapper extends BaseMapper<BizConfig> {
	/**
	 * 根据所属应用名配置
	 * @param appName
	 * @return
	 */
	BizConfig findByAppName(String appName);
}