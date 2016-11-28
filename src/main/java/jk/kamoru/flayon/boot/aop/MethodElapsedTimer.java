package jk.kamoru.flayon.boot.aop;

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
	
	@Around("execution(* jk.kamoru.flayon..*.*Service*.*(..)) or "
		  + "execution(* jk.kamoru.flayon..*.*Repository.*(..))")
	public Object elapsedTimePrint(ProceedingJoinPoint joinPoint) throws Throwable {
		if (log.isDebugEnabled())
			return elapsedPrint(joinPoint);
		else
			return joinPoint.proceed();
	}

	@Around("execution(* jk.kamoru.flayon..*.VideoBatch.*(..))")
	public Object videoBatchElapsedTimePrint(ProceedingJoinPoint joinPoint) throws Throwable {
		return elapsedPrint(joinPoint);
	}

	private Object elapsedPrint(ProceedingJoinPoint joinPoint) throws Throwable {
		String signature = joinPoint.getSignature().toShortString();
		String target = joinPoint.getTarget().getClass().getSimpleName();
		StopWatch sw = new StopWatch(String.format("%s -> %s", signature, target));
        try {
            sw.start();
            return joinPoint.proceed();
        } finally {
            sw.stop();
           	log.debug("[{}] Elapsed time = {}ms", sw.getId(), sw.getTotalTimeMillis());
        }
	}
}
