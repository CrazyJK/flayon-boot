package jk.kamoru.flayon.boot.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MappingJackson2XmlView;

import jk.kamoru.flayon.boot.aop.AccessLogRepository;
import jk.kamoru.flayon.boot.aop.HandlerAccessLogger;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

	@Autowired AccessLogRepository accessLogRepository;
	@Value("${use.repository.accesslog}") boolean useAccesslogRepository;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("home");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/flayon/faceoff").setViewName("flayon/faceoff");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new HandlerAccessLogger().setRepository(accessLogRepository, useAccesslogRepository));
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor(){
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		//request로 넘어오는 language parameter를 받아서 locale로 설정 한다.
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}

	@Bean(name = "localeResolver")
	public LocaleResolver sessionLocaleResolver() {
		// 세션 기준으로 로케일을 설정 한다.
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		// 쿠키 기준(세션이 끊겨도 브라우져에 설정된 쿠키 기준으로)
		// CookieLocaleResolver localeResolver = new CookieLocaleResolver();

		// 최초 기본 로케일을 강제로 설정이 가능 하다.
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.ignoreAcceptHeader(true).defaultContentType(MediaType.TEXT_HTML);
	}
	
	@Bean
	public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
		
		List<ViewResolver> resolvers = new ArrayList<ViewResolver>();
		resolvers.add(new ViewResolver() {
			@Override
			public View resolveViewName(String viewName, Locale locale) throws Exception {
				MappingJackson2JsonView view = new MappingJackson2JsonView();
				view.setPrettyPrint(true); 
				return view;
			}
		});
		resolvers.add(new ViewResolver() {
			@Override
			public View resolveViewName(String viewName, Locale locale) throws Exception {
				MappingJackson2XmlView view = new MappingJackson2XmlView();
				view.setPrettyPrint(true); 
				return view;
			}
		});

	    ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
	    resolver.setViewResolvers(resolvers);
	    resolver.setContentNegotiationManager(manager);
	    return resolver;
	  }
}
