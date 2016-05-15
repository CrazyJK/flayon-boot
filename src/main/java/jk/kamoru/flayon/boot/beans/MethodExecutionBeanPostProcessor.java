package jk.kamoru.flayon.boot.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Method execution BeanPostProcessor
 * <p>빈 생성후 설정에의해 채택된 메서드를 수행한다.
 * <pre>
 * &lt;bean class="{@link jk.crazy.flayon.boot.beans.kamoru.spring.MethodExecutionBeanPostProcessor}" &gt;
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

	private static int count = 0;

	/**
	 * set bean infomation. {key = beanName, value = methodName}
	 * 
	 * @param beans
	 */
	public void setBeans(Map<String, String> beans) {
		Assert.notNull(beans, "'beans' must not be null");
		this.beans = beans;
		log.info("BeanPostProcess set beans {}", beans);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		// Nothing to do
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if (beans == null)
			return bean;
		if (beans.keySet().contains(beanName)) {
			String methodName = beans.get(beanName);
			log.info("BeanPostProcess {} attempt to invoke {}.{}", ++count, beanName, methodName);

			try {
				Method method = bean.getClass().getDeclaredMethod(methodName);
				method.invoke(bean);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error("BeanPostProcess error", e);
			}
			
			log.info("BeanPostProcess {} completed to invoke {}.{}", count, beanName, methodName);
		}
		return bean;
	}

}
