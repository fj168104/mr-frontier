package com.mr.framework.frontier;

import com.mr.framework.frontier.module.BizConfig;
import com.mr.framework.frontier.module.BizRecord;
import com.mr.framework.frontier.service.FilterService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>pre过滤器， 请求前执行</p>
 */
public class PreCommonFilter extends ZuulFilter {

	private final Logger logger = LoggerFactory.getLogger(PreCommonFilter.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private FilterService filterService;

	@Override
	public String filterType() {
		return "pre"; // 可以在请求被路由之前调用
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
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();

		logger.info("--->>> TokenFilter {},{},{}", request.getMethod(), request.getRequestURL().toString(), request.getRequestURI());


		if (request.getRequestURI().startsWith(ProtocalType.REST.getName())) {
			//内部系统调用外部服务的逻辑， 包括记录监控信息、审计、业务逻辑都在这里
			logger.info("rest 调用");
			String appName = request.getRequestURI().replace(ProtocalType.REST.getName(), "")
					.replace("/", "");

			BizConfig bizConfig = filterService.findBizConfig(appName);


			BizRecord bizRecord = new BizRecord();

			bizRecord.setAppName(appName);
			bizRecord.setCallType(0);
			bizRecord.setMethodName(request.getRequestURL().toString());
			bizRecord.setRequestMethod(request.getMethod());
			bizRecord.setRequestParams(showParams(request));
			bizRecord.setRequestIp(getRemoteHost(request));
			bizRecord.setRequestUri(bizConfig.getUrl());
			bizRecord.setCreateTime(new Date());
			long startTime = System.currentTimeMillis();

			HttpServletResponse response = ctx.getResponse();
			response.setCharacterEncoding("utf-8");  //设置字符集
			response.setContentType("text/html; charset=utf-8"); //设置相应格式
			response.setStatus(200);
			ctx.setSendZuulResponse(false); //不进行路由
			try {
//				String resBody = restTemplate.getForObject("https://www.sojson.com/open/api/weather/json.shtml?city=上海", String.class);
				String resBody = restTemplate.getForObject(bizConfig.getUrl() + bizRecord.getRequestParams(), String.class);

				response.getWriter().write(resBody); //响应体
				bizRecord.setCallResult(0);
				bizRecord.setTimeConsuming(System.currentTimeMillis() - startTime);
				bizRecord.setModifyTime(new Date());
			} catch (IOException e) {
				logger.error("response io异常");
				bizRecord.setCallResult(1);
				bizRecord.setTimeConsuming(System.currentTimeMillis() - startTime);
				bizRecord.setModifyTime(new Date());
				e.printStackTrace();
			} finally {
				filterService.saveBizRecord(bizRecord);
			}
			ctx.setResponse(response);
			return null;
		} else if (request.getRequestURI().startsWith(ProtocalType.RPC.getName())) {
			//这里是内部的RPC服务互相调用的处理
			//TODO 待详细实现
			logger.info("rpc 调用");
			HttpServletResponse response = ctx.getResponse();
			response.setCharacterEncoding("utf-8");  //设置字符集
			response.setContentType("text/html; charset=utf-8"); //设置相应格式
			response.setStatus(200);
			ctx.setSendZuulResponse(false); //不进行路由
			try {
				response.getWriter().write("Hello, This is dubbo api"); //响应体
			} catch (IOException e) {
				logger.error("response io异常");
				e.printStackTrace();
			}
			ctx.setResponse(response);
			return null;
		} else if (request.getRequestURI().startsWith(ProtocalType.OPENAPI.getName())) {
			//外部服务调用内部提供的api的实现，都要通过这里转发
			//TODO 待实现

			ctx.setSendZuulResponse(true); //对请求进行路由
			ctx.setResponseStatusCode(200);
			ctx.set("isSuccess", true);
			return null;
		} else {
			//未实现的协议， 使用直接报错
			ctx.setSendZuulResponse(false); //不对其进行路由
			ctx.setResponseStatusCode(400);
			ctx.setResponseBody("ProtocalType is error");
			ctx.set("isSuccess", false);
			return null;
		}

	}

	public enum ProtocalType {
		REST("/mr/rest"), RPC("/mr/rpc"), OPENAPI("/openapi");
		private String name;

		private ProtocalType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private String getRemoteHost(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	private String showParams(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				if (paramValue.length() != 0) {
					map.put(paramName, paramValue);
				}
			}
		}

		if (map == null || map.size() == 0) return "";

		StringBuilder sb = new StringBuilder();
		for (Map.Entry entry : map.entrySet()) {

			if (!sb.toString().equals("")) {
				sb.append("&");
			}
			sb.append(entry.getKey() + "=" + entry.getValue());
		}

		return "?" + sb;
	}


}