package com.jk.modules.oti.controller;

import com.github.pagehelper.PageInfo;
import com.jk.common.annotation.OperationLog;
import com.jk.common.base.controller.BaseController;
import com.jk.common.security.token.FormToken;
import com.jk.modules.oti.model.OtiConfig;
import com.jk.modules.oti.service.OtiConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 报文解析配置管理
 *
 * @author fengj
 * Created by Mr on 2018/4/13.
 */
@Controller
@RequestMapping("/admin/oti/config")
public class OtiConfigController extends BaseController {
	private static final String BASE_PATH = "admin/oti/";

	@Resource
	private OtiConfigService otiConfigService;

	/**
	 * 分页查询调用配置列表
	 *
	 * @param pageNum  当前页码
	 * @param msgId    传输消息ID
	 * @param modelMap
	 * @return
	 */
	@RequiresPermissions("oti-config:list")
	@GetMapping
	public String list(
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			String msgId, ModelMap modelMap) throws Exception {
		try {
			log.debug("分页查询解析配置列表参数! pageNum = {}, msgId = {}", pageNum, msgId);
			PageInfo<OtiConfig> pageInfo = otiConfigService.findPage(pageNum, PAGESIZE, msgId);
			log.info("分页查询解析配置列表结果！ pageInfo = {}", pageInfo);
			modelMap.put("pageInfo", pageInfo);
			modelMap.put("msgId", msgId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BASE_PATH + "otiConfig-list";
	}

	/**
	 * 根据主键ID删除解析配置
	 *
	 * @param id
	 * @return
	 */
	@OperationLog(value = "删除解析配置")
	@RequiresPermissions("oti-config:delete")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") Long id) {
		log.debug("删除解析配置! id = {}", id);

		OtiConfig otiConfig = otiConfigService.findById(id);
		if (null == otiConfig) {
			log.info("删除的解析配置不存在! id = {}", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("解析配置不存在!");
		}

		otiConfigService.deleteById(id);
		log.info("删除解析配置成功! id = {}", id);
		return ResponseEntity.ok("已删除!");
	}

	/**
	 * 批量删除解析配置
	 *
	 * @param ids
	 * @return
	 */
	@OperationLog(value = "批量删除解析配置")
	@RequiresPermissions("oti-config:delete")
	@DeleteMapping(value = "/batch/{ids}")
	public ResponseEntity<Void> deleteBatch(@PathVariable("ids") List<Object> ids) {
		log.debug("批量删除解析配置! ids = {}", ids);

		if (null == ids) {
			log.info("批量删除解析配置不存在! ids = {}", ids);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		otiConfigService.deleteByCondition(OtiConfig.class, "id", ids);
		log.info("批量删除解析配置成功! ids = {}", ids);

		return ResponseEntity.ok(null);
	}

	/**
	 * 禁用|启用
	 *
	 * @param id
	 * @return
	 */
	@OperationLog(value = "禁用|启用解析配置")
	@RequiresPermissions("oti-config:status")
	@PutMapping(value = "/status/{id}")
	public ResponseEntity<String> updateStatus(@PathVariable("id") Long id) {
		log.debug("禁用|启用解析配置参数! id = {}", id);
		OtiConfig otiConfig = otiConfigService.findById(id);

		if (null == otiConfig) {
			log.info("解析配置不存在! id = {}", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("解析配置不存在!");
		}

		//禁用启用
		otiConfig.setStatus(otiConfig.getStatus() == 1 ? 0 : 1);
		otiConfigService.updateSelective(otiConfig);

		log.info("禁用|启用解析配置成功! id = {}", id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * 跳转到解析配置管理添加页面
	 *
	 * @return
	 */
	@FormToken(save = true)
	@RequiresPermissions("oti-config:create")
	@GetMapping(value = "/add")
	public String add(ModelMap modelMap) {
		log.info("跳转到解析配置添加页面!");
		return BASE_PATH + "otiConfig-add";
	}

	/**
	 * 添加解析配置
	 *
	 * @return
	 */
	@FormToken(remove = true)
	@OperationLog(value = "添加解析配置")
	@RequiresPermissions("oti-config:create")
	@ResponseBody
	@PostMapping
	public ModelMap saveUser(OtiConfig otiConfig) throws Exception {
		ModelMap messagesMap = new ModelMap();

		log.debug("添加解析配置参数! otiConfig = {}", otiConfig);
		if (otiConfigService.saveOtiConfig(otiConfig)) {
			log.info("添加解析配置成功! otiConfigId = {}", otiConfig.getId());
			messagesMap.put("status", SUCCESS);
			messagesMap.put("message", "添加成功!");
			return messagesMap;
		}
		log.info("添加解析配置失败, 但没有抛出异常! otiConfigId = {}", otiConfig.getId());
		messagesMap.put("status", FAILURE);
		messagesMap.put("message", "添加失败!");
		return messagesMap;
	}

	/**
	 * 跳转到解析配置管理编辑页面
	 *
	 * @return
	 */
	@FormToken(save = true)
	@RequiresPermissions("oti-config:update")
	@GetMapping(value = "/edit/{id}")
	public String edit(@PathVariable("id") Long id, ModelMap modelMap) {
		OtiConfig otiConfig = otiConfigService.findById(id);
		log.info("跳转到编辑页面！id = {}", id);
		modelMap.put("model", otiConfig);
		return BASE_PATH + "otiConfig-edit";
	}

	/**
	 * 更新解析配置管理信息
	 *
	 * @param id
	 * @param otiConfig
	 * @return
	 */
	@FormToken(remove = true)
	@OperationLog(value = "编辑解析配置")
	@RequiresPermissions("oti-config:update")
	@ResponseBody
	@PutMapping(value = "/{id}")
	public ModelMap updateUser(@PathVariable("id") Long id, OtiConfig otiConfig) throws Exception {
		ModelMap messagesMap = new ModelMap();
		log.debug("编辑解析配置参数! id= {}, otiConfig = {}", id, otiConfig);

		OtiConfig otiConfigPersist = otiConfigService.findById(id);
		if (null == otiConfigPersist) {
			log.info("编辑解析配置不存在! id = {}", id);
			messagesMap.put("status", FAILURE);
			messagesMap.put("message", "解析配置不存在!");
			return messagesMap;
		}

		otiConfigPersist.setMsgId(otiConfig.getMsgId());
		otiConfigPersist.setMsgNameDesp(otiConfig.getMsgNameDesp());
		otiConfigPersist.setCharset(otiConfig.getCharset());
		Boolean flag = otiConfigService.updateOtiConfig(otiConfigPersist);
		if (flag) {
			log.info("编辑解析配置成功! id= {}, otiConfig = {}", id, otiConfig);
			messagesMap.put("status", SUCCESS);
			messagesMap.put("message", "编辑成功!");
			return messagesMap;
		}
		log.info("编辑解析配置失败,但没有抛出异常 ! id= {}, otiConfig = {}", id, otiConfig);
		messagesMap.put("status", FAILURE);
		messagesMap.put("message", "编辑失败!");
		return messagesMap;
	}

	/**
	 * 检验解析配置是否存在
	 *
	 * @param msgId
	 * @return
	 */
	@ResponseBody
	@GetMapping(value = "/isExist")
	public Boolean isExist(Long id, String msgId) throws Exception {
		boolean flag = true;
		log.debug("检验解析配置是否存在参数! id= {}, msgId= {}", id, msgId);
		OtiConfig otiConfig = otiConfigService.findByMsgId(msgId);
		if (null != otiConfig && !otiConfig.getId().equals(id)) {
			flag = false;
		}
		log.info("检验解析配置是否存在结果! flag = {}", flag);
		return flag;
	}

}