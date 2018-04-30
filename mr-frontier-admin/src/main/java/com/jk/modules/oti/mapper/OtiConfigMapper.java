package com.jk.modules.oti.mapper;

import com.jk.common.base.mapper.BaseMapper;
import com.jk.modules.oti.model.OtiConfig;

public interface OtiConfigMapper extends BaseMapper<OtiConfig> {

	/**
	 * 根据传输消息ID配置
	 * @param msgId
	 * @return
	 */
	OtiConfig findByMsgId(String msgId);
}