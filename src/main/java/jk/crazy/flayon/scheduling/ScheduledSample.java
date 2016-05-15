package jk.crazy.flayon.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * scheduling method<br>
 * <code>@</code>EnableScheduling 설정 필요
 * <pre>
 *   <code>@</code>SpringBootApplication
 *   <code>@</code>EnableScheduling
 *   public class FlayOnApplication {
 *   ...
 * </pre>
 * @author kamoru
 *
 */
@Slf4j
@Component
public class ScheduledSample {
	
	@Scheduled(fixedDelay = 1000 * 60 * 5)
	public void iAmAlive() {
		log.info("I am alive!!!");
	}
}
