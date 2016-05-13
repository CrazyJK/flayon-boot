package jk.crazy.flayon.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchedulingSample {
	
	@Scheduled(fixedDelay = 1000 * 60)
	public void iAmAlice() {
		log.info("I am alive!!!");
	}
}
