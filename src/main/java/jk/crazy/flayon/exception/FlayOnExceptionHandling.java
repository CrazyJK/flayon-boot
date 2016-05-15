package jk.crazy.flayon.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class FlayOnExceptionHandling {

	@ExceptionHandler(value = FlayOnException.class)
	public ModelAndView exception(FlayOnException exception, WebRequest request) {
		ModelAndView modelAndView = new ModelAndView("error/flayon");
		modelAndView.addObject(exception);
		return modelAndView;
	}

}