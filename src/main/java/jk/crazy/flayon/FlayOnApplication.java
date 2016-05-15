package jk.crazy.flayon;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import jk.crazy.flayon.beans.MethodExecutionBeanPostProcessor;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
@EnableAsync
public class FlayOnApplication {

	public static final long SERIAL_VERSION_UID = 0x23123;
	
	public static void main(String[] args) {
		SpringApplication.run(FlayOnApplication.class, args);
	}
	
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
