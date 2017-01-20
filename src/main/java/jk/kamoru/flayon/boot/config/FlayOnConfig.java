package jk.kamoru.flayon.boot.config;

import java.util.HashMap;
import java.util.Map;

import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jk.kamoru.flayon.boot.beans.MethodExecutionBeanPostProcessor;
import jk.kamoru.flayon.boot.vertx.VertxServer;

@Configuration
public class FlayOnConfig {

	@Bean
	public BeanPostProcessor methodExecutionBeanPostProcessor() {
		MethodExecutionBeanPostProcessor processor = new MethodExecutionBeanPostProcessor();
		Map<String, String> beans = new HashMap<>();
		beans.put("infoWatch", "start");
		beans.put("methodExecutionSample", "loadInitData");
		
		processor.setBeans(beans);
		return processor;
	}

	@Profile("jsp")
	@Bean
	public FilterRegistrationBean siteMeshFilter() {
		FilterRegistrationBean filter = new FilterRegistrationBean();
		filter.setFilter(new ConfigurableSiteMeshFilter());
		filter.addInitParameter(ConfigurableSiteMeshFilter.CONFIG_FILE_PARAM, "/WEB-INF/sitemesh/sitemesh3.xml");
		return filter;
	}

	@Bean
	public VertxServer vertxServer() {
		return new VertxServer();
	}
}
