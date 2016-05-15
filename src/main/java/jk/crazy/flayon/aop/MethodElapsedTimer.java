package jk.crazy.flayon.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

/**
 * AOP Method intercepter<br>
 * <code>@</code>EnableAspectJAutoProxy 설정 필요
 * <pre>
 *   <code>@</code>SpringBootApplication
 *   <code>@</code>EnableAspectJAutoProxy
 *   public class FlayOnApplication {
 *   ...
 * </pre>
 * @author kamoru
 *
 */
@Aspect
@Component
@Slf4j
public class MethodElapsedTimer {
	
	@Around("execution(* jk.crazy.flayon.*.*Service*.*(..))")
	public Object service(ProceedingJoinPoint joinPoint) throws Throwable {
		return elapsedTime(joinPoint);
	}

	@Around("execution(* jk.crazy.flayon.*.*Repository.*(..))")
	public Object repository(ProceedingJoinPoint joinPoint) throws Throwable {
		return elapsedTime(joinPoint);
	}

	private Object elapsedTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature().toShortString();
        String target = joinPoint.getTarget().getClass().getSimpleName();
        StopWatch sw = new StopWatch(String.format("%s %s", signature, target));
        try {
            sw.start();
            return joinPoint.proceed();
        } finally {
            sw.stop();
//            log.info("{}", sw.shortSummary());
            log.info("{} elapsed time = {}ms", sw.getId(), sw.getTotalTimeMillis());
        }
	}
}
