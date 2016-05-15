package jk.kamoru.flayon.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "jk.kamoru.flayon.boot")
@EnableScheduling
@EnableAspectJAutoProxy
@EnableAsync
public class FlayOnApplication {

	public static final long SERIAL_VERSION_UID = 0x2316CF8C;
	
	public static void main(String[] args) {
		SpringApplication.run(FlayOnApplication.class, args);
	}

}
