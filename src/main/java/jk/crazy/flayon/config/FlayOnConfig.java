package jk.crazy.flayon.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jk.crazy.flayon.beans.MethodExecutionBeanPostProcessor;

@Configuration
public class FlayOnConfig {

	@Bean
	public BeanPostProcessor methodExecutionBeanPostProcessor() {
		MethodExecutionBeanPostProcessor processor = new MethodExecutionBeanPostProcessor();
		Map<String, String> beans = new HashMap<>();
		beans.put("asyncWatchDirectorySample", "start");
		beans.put("methodExecutionSample", "loadInitData");
		
		processor.setBeans(beans);
		return processor;
	}

}
