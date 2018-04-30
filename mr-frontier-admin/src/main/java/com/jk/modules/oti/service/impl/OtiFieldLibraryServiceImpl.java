package com.jk.modules.oti.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk.common.base.service.impl.BaseServiceImpl;
import com.jk.modules.oti.mapper.OtiFieldLibraryMapper;
import com.jk.modules.oti.model.OtiFieldLibrary;
import com.jk.modules.oti.service.OtiFieldLibraryService;
import com.jk.modules.sys.vo.TreeNode;
import com.xiaoleilu.hutool.util.StrUtil;
import io.jsonwebtoken.lang.Collections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by fengj on 2018/4/30.
 */
@Transactional
@Service
public class OtiFieldLibraryServiceImpl extends BaseServiceImpl<OtiFieldLibrary> implements OtiFieldLibraryService {

	//demo 消息ID
	private static String MSG_ID_DEMO = "demo";
	@Resource
	private OtiFieldLibraryMapper otiFieldLibraryMapper;

	@Transactional(readOnly = true)
	@Override
	public PageInfo<OtiFieldLibrary> findPage(Integer pageNum, Integer pageSize, String msgId) {
		Example example = new Example(OtiFieldLibrary.class);
		Criteria criteria = example.createCriteria();
		if (StrUtil.isNotEmpty(msgId)) {
			criteria.andEqualTo("msgId", msgId);
		} else {
			criteria.andEqualTo("msgId", MSG_ID_DEMO);
		}
		PageHelper.startPage(pageNum, pageSize);
		List<OtiFieldLibrary> otiFieldLibraryList = this.selectByExample(example);
		return new PageInfo<OtiFieldLibrary>(otiFieldLibraryList);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OtiFieldLibrary> findListByType(int type, String msgId) {
		OtiFieldLibrary otiFieldLibrary = new OtiFieldLibrary();
		otiFieldLibrary.setMsgId(msgId);
		otiFieldLibrary.setDataType(type);
		return this.findListByWhere(otiFieldLibrary);
	}

	@Transactional(readOnly = true)
	@Override
	public List<TreeNode> findTreeList(String msgId) {
		return otiFieldLibraryMapper.findTreeListByMsgId(msgId);
	}

	@Override
	public Boolean deleteFieldAndArrayOrObject(Long fieldId) {
		//删除权限
		int count1 = this.deleteById(fieldId);
		Example example = new Example(OtiFieldLibrary.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("parentId", fieldId);

		List<OtiFieldLibrary> otiFieldLibraries = this.selectByExample(example);
		if (!Collections.isEmpty(otiFieldLibraries)) {
			for (OtiFieldLibrary otiFieldLibrary : otiFieldLibraries) {
				return deleteFieldAndArrayOrObject(otiFieldLibrary.getId());
			}
		}
		return count1 == 1;
	}

}
