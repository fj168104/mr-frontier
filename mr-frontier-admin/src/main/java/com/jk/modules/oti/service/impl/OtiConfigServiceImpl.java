package com.jk.modules.oti.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk.common.base.service.impl.BaseServiceImpl;
import com.jk.modules.oti.model.OtiConfig;
import com.jk.modules.oti.mapper.OtiConfigMapper;
import com.jk.modules.oti.service.OtiConfigService;
import com.xiaoleilu.hutool.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created by feng on 18-3-3
 */

@Transactional
@Service
public class OtiConfigServiceImpl extends BaseServiceImpl<OtiConfig> implements OtiConfigService {

	@Autowired
	private OtiConfigMapper otiConfigMapper;

	@Override
	public PageInfo<OtiConfig> findPage(Integer pageNum, Integer pageSize, String msgId) throws Exception {
		Example example = new Example(OtiConfig.class);
		Example.Criteria criteria = example.createCriteria();
		if (StringUtils.isNotEmpty(msgId)) {
			criteria.andLike("msgId", "%" + msgId + "%");
		}

		//倒序
		example.orderBy("createTime").desc();

		//分页
		PageHelper.startPage(pageNum, pageSize);
		List<OtiConfig> otiConfigList = this.selectByExample(example);

		return new PageInfo<OtiConfig>(otiConfigList);
	}


	@Override
	public OtiConfig findByMsgId(String msgId) throws Exception {
		if (StrUtil.isEmpty(msgId)) return null;
		return otiConfigMapper.findByMsgId(msgId);
	}

	@Override
	public Boolean saveOtiConfig(OtiConfig otiConfig) throws Exception {
		otiConfig.setStatus(1);
		otiConfig.setCreateTime(new Date());
		otiConfig.setModifyTime(otiConfig.getCreateTime());
		return otiConfigMapper.insert(otiConfig) > 0 ? true : false;
	}

	@Override
	public Boolean updateOtiConfig(OtiConfig otiConfig) throws Exception {
		otiConfig.setModifyTime(new Date());
		return otiConfigMapper.updateByPrimaryKey(otiConfig) > 0 ? true : false;
	}


}
