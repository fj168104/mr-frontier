package com.jk.modules.oti.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jk.common.base.service.impl.BaseServiceImpl;
import com.jk.modules.oti.mapper.OtiFieldLibraryMapper;
import com.jk.modules.oti.model.OtiConfig;
import com.jk.modules.oti.mapper.OtiConfigMapper;
import com.jk.modules.oti.model.OtiFieldLibrary;
import com.jk.modules.oti.service.OtiConfigService;
import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by feng on 18-3-3
 */

@Transactional
@Service
@Slf4j
public class OtiConfigServiceImpl extends BaseServiceImpl<OtiConfig> implements OtiConfigService {

	@Autowired
	private OtiConfigMapper otiConfigMapper;
	@Autowired
	private OtiFieldLibraryMapper otiFieldLibraryMapper;


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

	@Override
	public int deleteCascadeByIds(List<Object> ids) {

		//删除字段配置
		for (Object id : ids) {
			OtiConfig otiConfig = findById((long) id);
			String msgId = otiConfig.getMsgId();
			Example example = new Example(OtiFieldLibrary.class);
			example.createCriteria().andEqualTo("msgId", msgId);
			otiFieldLibraryMapper.deleteByExample(example);
		}
		//删除配置
		return this.deleteByCondition(OtiConfig.class, "id", ids);
	}

	@Override
	public byte[] createXmlConfig(List<Object> ids) {
		String encoding = "UTF-8";
		Document document = DocumentHelper.createDocument();

		Element root = document.addElement("AppInstance");
		Element messageList = root.addElement("MessageList");
		for (Object id : ids) {
			OtiConfig otiConfig = this.findById(Long.parseLong(String.valueOf(id)));
			if (Objects.isNull(otiConfig)) continue;
			Element message = messageList.addElement("Message")
					.addAttribute("Id", otiConfig.getMsgId())
					.addAttribute("Description", otiConfig.getMsgNameDesp())
					.addAttribute("CharSet", otiConfig.getCharset());
			buildMessage(message, otiConfig.getMsgId(), null);

		}
		return writeDocument(document, encoding);
	}

	private void buildMessage(Element message, String msgId, Long parentId) {
		List<OtiFieldLibrary> otiFieldLibraryList = otiFieldLibraryMapper.findFieldLibraryByMsgId(msgId);
		//取出parentId下的节点
		List<OtiFieldLibrary> otiFieldLibrarys = Lists.newArrayList();
		for (OtiFieldLibrary otiFieldLibrary : otiFieldLibraryList) {
			if (Objects.isNull(parentId) &&
					Objects.isNull(otiFieldLibrary.getParentId())) {
				otiFieldLibrarys.add(otiFieldLibrary);
			} else if (!Objects.isNull(parentId)
					&& !Objects.isNull(otiFieldLibrary.getParentId())
					&& parentId == otiFieldLibrary.getParentId()) {
				otiFieldLibrarys.add(otiFieldLibrary);
			}
		}

		for (OtiFieldLibrary otiFieldLibrary : otiFieldLibrarys) {
			Element field = message.addElement("Field")
					.addAttribute("FieldTag", otiFieldLibrary.getFieldTag())
					.addAttribute("Description", otiFieldLibrary.getFieldDesp());
			if (DataType.ARRAY.code == otiFieldLibrary.getDataType() && StrUtil.isEmpty(otiFieldLibrary.getTableField())) {
				field.addAttribute("TableField", otiFieldLibrary.getTableField());
				buildMessage(field.addElement("Message"), msgId, otiFieldLibrary.getParentId());
			} else if (DataType.OBJECT.code == otiFieldLibrary.getDataType()) {
				buildMessage(field.addElement("Message"), msgId, otiFieldLibrary.getParentId());
			} else {
				message.addAttribute("Length", otiFieldLibrary.getFieldLength())
						.addAttribute("DefaultValue", otiFieldLibrary.getFieldDefault())
						.addAttribute("IsRequire", otiFieldLibrary.getIsRequire() ? "Y" : "N");
			}
		}
	}

	/**
	 * 把document对象写入新的文件
	 *
	 * @param document
	 * @throws Exception
	 */
	public byte[] writeDocument(Document document, String charset) {
		byte[] b = null;
		String filePath = System.getProperty("java.io.tmpdir")
				+ File.separator
				+ UUID.randomUUID().toString() + ".xml";
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 设置编码
			format.setEncoding(charset);
			XMLWriter writer = new XMLWriter(new OutputStreamWriter(
					new FileOutputStream(new File(filePath)), charset), format);
			// 写入
			writer.write(document);
			// 立即写入
			writer.flush();
			// 关闭操作
			writer.close();

			b = IoUtil.readBytes(new FileInputStream(filePath));

			FileUtil.del(filePath);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return b;
	}

	@Getter
	static enum DataType {

		STRING(1, "string"),
		INT(2, "int"),
		DOUBLE(3, "double"),
		OBJECT(4, "object"),
		ARRAY(5, "array");

		private final int code;
		private final String name;

		DataType(int code, String name) {
			this.code = code;
			this.name = name;
		}
	}
}
