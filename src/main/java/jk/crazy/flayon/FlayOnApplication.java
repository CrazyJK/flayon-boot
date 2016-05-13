package jk.crazy.flayon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlayOnApplication {

	public static final long SERIAL_VERSION_UID = 0x23123;
	
	public static void main(String[] args) {
		SpringApplication.run(FlayOnApplication.class, args);
	}
}
