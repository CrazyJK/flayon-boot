package jk.crazy.flayon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
@EnableAsync
public class FlayOnApplication {

	public static final long SERIAL_VERSION_UID = 0x2316CF8C;
	
	public static void main(String[] args) {
		SpringApplication.run(FlayOnApplication.class, args);
	}

}
