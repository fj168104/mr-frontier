package com.jk.modules.alerm.controller;

import com.github.pagehelper.PageInfo;
import com.jk.common.base.controller.BaseController;
import com.jk.modules.job.model.ScheduleJobLog;
import com.jk.modules.job.service.ScheduleJobLogService;
import com.jk.modules.log.model.Log;
import com.jk.modules.log.service.LogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * alerm
 * Created by fengj on 2018/4/30.
 */
@Controller
@RequestMapping("/admin/alerm")
public class AlermController extends BaseController {

	private static final String BASE_PATH = "admin/alerm/";

	@Resource
	private LogService logService;

	/**
	 * sms
	 * @return
	 */
	@GetMapping(value = "/sms")
	@RequiresPermissions("alerm-mail:config")
	public String configSms(ModelMap modelMap){

		return BASE_PATH+"sms-config";
	}

	/**
	 * mail
	 * @return
	 */
	@GetMapping(value = "/mail")
	@RequiresPermissions("alerm-sms:config")
	public String configMail(ModelMap modelMap){

		return BASE_PATH+"sms-config";
	}

	/**
	 * log
	 * @return
	 */
	@GetMapping(value = "/log")
	@RequiresPermissions("alerm-log:list")
	public String list(
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			String username, String startTime, String endTime, ModelMap modelMap) {
		try {
			log.debug("分页查询日志列表参数! pageNum = {}, username = {}, username = {}, startTime = {}, endTime = {}", pageNum, username, startTime, endTime);
			PageInfo<Log> pageInfo = logService.findPage(pageNum, PAGESIZE, username, startTime, endTime);
			log.info("分页查询日志列表结果！ pageInfo = {}", pageInfo);
			modelMap.put("pageInfo", pageInfo);
			modelMap.put("username", username);
			modelMap.put("startTime", startTime);
			modelMap.put("endTime", endTime);
		} catch (Exception e) {
			log.error("分页查询日志列表失败! e = {}", e);
		}
		return "admin/log/log-list";
	}

}
