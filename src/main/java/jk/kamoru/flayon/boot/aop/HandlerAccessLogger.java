package jk.kamoru.flayon.boot.aop;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * handler 실행 전, 후, 완료 시점에 accesslog형식으로 로그에 기록한다.
 * <p> 시점은 {@link #setWhen(WHEN)}으로 결정. default {@link WHEN#AFTER}
 * <pre>
 *  <code>@</code>Override
 *  public void addInterceptors(InterceptorRegistry registry) {
 *  	registry.addInterceptor(new HandlerAccessLogger().setWhen(WHEN.AFTER));
 *  }
 * </pre>
 * @author kamoru
 *
 */
@Slf4j
public class HandlerAccessLogger implements HandlerInterceptor {

	@Autowired AccessLogRepository accessLogRepository;
	
	private long startTime;
	
	/**
	 * {@link #PRE}, {@link #POST}, {@link #AFTER}
	 * @author kamoru
	 */
	public enum WHEN {
		PRE, POST, AFTER
	}
	private WHEN when = WHEN.AFTER;
	
	/**
	 * 로그를 찍을 시점.
	 * @param when
	 */
	public HandlerAccessLogger setWhen(WHEN when) {
		this.when = when;
		return this;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.trace("preHandle");
		startTime = System.currentTimeMillis();
		if (when == WHEN.PRE)
			log.info("pre : {}", getAccesslog(request, response, handler, null, null));
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		log.trace("postHandle");
		if (when == WHEN.POST)
			log.info("post : {}", getAccesslog(request, response, handler, modelAndView, null));
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		log.trace("afterCompletion START");
		if (when == WHEN.AFTER)
			log.info("{}", getAccesslog(request, response, handler, null, ex));
		log.trace("afterCompletion END");
	}

	private String getAccesslog(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView, Exception ex) {
		long elapsedtime = System.currentTimeMillis() - startTime;

		String handlerlInfo = "";
		// for Controller
		if (handler instanceof org.springframework.web.method.HandlerMethod) {
			HandlerMethod method = (HandlerMethod) handler;
			handlerlInfo = String.format("%s.%s", method.getBean().getClass().getSimpleName(), method.getMethod().getName());
		} 
		// for static resources. No additional information
		else if (handler instanceof ResourceHttpRequestHandler) {
			// do nothing
		}
		// another handler 
		else {
			handlerlInfo = String.format("%s", handler);
		}

		// for modelAndView
		String modelAndViewInfo = "";
		if (modelAndView != null) {
			String viewName = modelAndView.getViewName();
			String modelNames = Arrays.toString(modelAndView.getModel().keySet().toArray(new String[0]));
			modelAndViewInfo = String.format("view=%s model=%s", viewName, modelNames);
		}
		
		String exceptionInfo = "";
		if (ex != null) {
			exceptionInfo = "Error : " + ex.getMessage();
		}
		
		String accesslog = String.format("[%s] %s %s %s %sms [%s] %s %s", 
				request.getRemoteAddr(), 
				request.getMethod(), 
				request.getRequestURI(),
				StringUtils.trimWhitespace(response.getContentType()), 
				elapsedtime,
				handlerlInfo,
				exceptionInfo,
				modelAndViewInfo
				);

		accessLogRepository.save(new AccessLog(
				new Date(),
				request.getRemoteAddr(),
				request.getMethod(), 
				request.getRequestURI(),
				StringUtils.trimWhitespace(response.getContentType()), 
				elapsedtime,
				handlerlInfo,
				exceptionInfo,
				modelAndViewInfo));
		
		return accesslog;
	}

	public HandlerInterceptor setRepository(AccessLogRepository accessLogRepository) {
		this.accessLogRepository = accessLogRepository;
		return this;
	}
}
