package com.mr.framework.frontier.service.impl;

import com.mr.framework.frontier.module.BizConfig;
import com.mr.framework.frontier.module.BizRecord;
import com.mr.framework.frontier.service.FilterService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by feng on 18-3-4
 */
@Service
public class FilterServiceImpl implements FilterService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean saveBizRecord(BizRecord bizRecord) {

		int ct = jdbcTemplate.update("insert into biz_record(app_name, " +
						"call_type, " +
						"method_name, " +
						"request_method, " +
						"request_params, " +
						"request_ip, " +
						"request_uri, " +
						"time_consuming, " +
						"call_result, " +
						"create_time, " +
						"modify_time) values(?, ?, ?, ?, ?,?, ?, ?, ?, ?,?)",
				bizRecord.getAppName(),
				bizRecord.getCallType(),
				bizRecord.getMethodName(),
				bizRecord.getRequestMethod(),
				bizRecord.getRequestParams(),
				bizRecord.getRequestIp(),
				bizRecord.getRequestUri(),
				bizRecord.getTimeConsuming(),
				bizRecord.getCallResult(),
				bizRecord.getCreateTime(),
				bizRecord.getModifyTime());
		return ct > 0;
	}

	@Override
	public BizConfig findBizConfig(String appName) {
		String sql = "select * from biz_config where app_name = ?";

		Map<String, Object> map = jdbcTemplate.queryForMap(sql, appName);
		BizConfig bizConfig = new BizConfig();
		bizConfig.setAppName(appName);
		bizConfig.setAppNameDesp(String.valueOf(map.get("app_name_desp")));
		bizConfig.setUrl(String.valueOf(map.get("URL")));
		bizConfig.setIsLock(Boolean.valueOf(String.valueOf(map.get("is_lock"))));

		return bizConfig;
	}

}
