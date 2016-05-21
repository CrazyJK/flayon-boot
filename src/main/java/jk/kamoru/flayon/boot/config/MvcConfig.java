package jk.kamoru.flayon.boot.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import jk.kamoru.flayon.boot.aop.HandlerAccessLogger;
import jk.kamoru.flayon.boot.aop.HandlerAccessLogger.WHEN;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("home");
		registry.addViewController("/login").setViewName("login");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new HandlerAccessLogger().setWhen(WHEN.AFTER));
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor(){
		LocaleChangeInterceptor localeChangeInterceptor=new LocaleChangeInterceptor();
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
}
