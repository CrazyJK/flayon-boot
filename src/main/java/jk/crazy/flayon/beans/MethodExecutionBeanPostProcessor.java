package jk.crazy.flayon.beans;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Method execution BeanPostProcessor
 * <p>빈 생성후 설정에의해 채택된 메서드를 수행한다.
 * <pre>
 * &lt;bean class="{@link jk.kamoru.spring.MethodExecutionBeanPostProcessor}" &gt;
 *   &lt;-- need to property 'beans' of Map type. {key=beanName, value=methodName} --&gt;
 *   &lt;property name="beans"&gt; 
 *     &lt;map&gt;
 *       &lt;entry key="videoSource" value="reload"/&gt;
 *       &lt;entry key="localImageSource" value="reload"/&gt;
 *     &lt;/map&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * @author kamoru
 *
 */
@Slf4j
public class MethodExecutionBeanPostProcessor implements BeanPostProcessor {

	private Map<String, String> beans;

	/**
	 * set bean infomation. {key = beanName, value = methodName}
	 * 
	 * @param beans
	 */
	public void setBeans(Map<String, String> beans) {
		Assert.notNull(beans, "'beans' must not be null");
		this.beans = beans;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		// Nothing to do
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
//		log.debug("postProcessAfterInitialization {}", beanName);
		if (beans.keySet().contains(beanName)) {
			String methodName = beans.get(beanName);
			try {
				log.info("attempt to invoke {}.{}", beanName, methodName);
//				log.debug(" class : {}", bean.getClass());
//				for (Method method : bean.getClass().getMethods()) {
//					log.debug("  method : {}", method.getName());
//				}

				Method method = bean.getClass().getDeclaredMethod(methodName);
				method.invoke(bean);
				log.info("completed to invoke {}.{}", beanName, methodName);
			} catch (Exception e) {
				log.error("method invocation error", e);
				throw new RuntimeException("method invocation error", e);
			}
		}
		return bean;
	}

}
