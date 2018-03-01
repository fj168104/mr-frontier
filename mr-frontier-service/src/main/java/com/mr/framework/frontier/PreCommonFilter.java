package com.mr.framework.frontier;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>pre过滤器， 请求前执行</p>
 */
public class PreCommonFilter extends ZuulFilter {

	private final Logger logger = LoggerFactory.getLogger(PreCommonFilter.class);

	@Autowired
	private RestTemplate restTemplate;

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
			logger.info("rest 调用");
			HttpServletResponse response = ctx.getResponse();
			response.setCharacterEncoding("utf-8");  //设置字符集
			response.setContentType("text/html; charset=utf-8"); //设置相应格式
			response.setStatus(200);
			ctx.setSendZuulResponse(false); //不进行路由
			try {
				String resBody = restTemplate.getForObject("https://www.sojson.com/open/api/weather/json.shtml?city=上海", String.class);
				response.getWriter().write(resBody); //响应体
			} catch (IOException e) {
				logger.error("response io异常");
				e.printStackTrace();
			}
			ctx.setResponse(response);
			return null;
		} else if (request.getRequestURI().startsWith(ProtocalType.RPC.getName())) {
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
			ctx.setSendZuulResponse(true); //对请求进行路由
			ctx.setResponseStatusCode(200);
			ctx.set("isSuccess", true);
			return null;
		} else {
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


}