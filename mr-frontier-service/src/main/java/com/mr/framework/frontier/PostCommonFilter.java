package com.mr.framework.frontier;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * <p>pre过滤器， 收到response后转发结果前执行</p>
 */
public class PostCommonFilter extends ZuulFilter {

	private final Logger logger = LoggerFactory.getLogger(PostCommonFilter.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public String filterType() {
		return "post"; // 可以在收到响应后调用
	}

	@Override
	public int filterOrder() {
		return 0; // filter执行顺序，通过数字指定 ,优先级为0，数字越大，优先级越低
	}

	@Override
	public boolean shouldFilter() {
		return true;// 是否执行该过滤器，此处为true，说明需要过滤
	}

	@Override
	public Object run() {
		RequestContext context = RequestContext.getCurrentContext();
		HttpServletResponse response = context.getResponse();
		try {
			InputStream stream = context.getResponseDataStream();
			if (StringUtils.isEmpty(stream)) return null;
//			String body = StreamUtils.copyToString(stream, Charset.forName("GBK"));
//			body = CharStreams.toString(new InputStreamReader(stream, "GBK"));

			String appName = "MR";
			int callType = 0;
			Date createTime = new Date();
			Date modifyTime = new Date();
			int ct = jdbcTemplate.update("insert into biz_record(app_name, call_type, create_time, modify_time) values(?, ?, ?, ?)",
					appName, callType, createTime, modifyTime);

			logger.info("--->>> body: {}", response.getStatus());
//			context.setResponseBody(body);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static final byte[] input2byte(InputStream inStream)
			throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}
}