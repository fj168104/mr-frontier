package com.mr.framework.frontier;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenFilter extends ZuulFilter {

	private final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

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

		logger.info("--->>> TokenFilter {},{},{}", request.getMethod(), request.getRequestURL().toString(),request.getRequestURI());

		String token = request.getParameter("token");// 获取请求的参数

		if (StringUtils.isNotBlank(token)) {
			ctx.setSendZuulResponse(true); //对请求进行路由
			ctx.setResponseStatusCode(200);
			ctx.set("isSuccess", true);
			return null;
		} else {
//			ctx.setSendZuulResponse(false); //不对其进行路由
//			ctx.setResponseStatusCode(400);
//			ctx.setResponseBody("token is empty");
//			ctx.set("isSuccess", false);

			//认证失败
			logger.error("token验证失败");
			HttpServletResponse response = ctx.getResponse();
			response.setCharacterEncoding("utf-8");  //设置字符集
			response.setContentType("text/html; charset=utf-8"); //设置相应格式
			response.setStatus(200);
			ctx.setSendZuulResponse(false); //不进行路由
			try {
				response.getWriter().write("token 验证失败"); //响应体
			} catch (IOException e) {
				logger.error("response io异常");
				e.printStackTrace();
			}
			ctx.setResponse(response);
			return null;
		}

//			return null;
//		}
	}

}