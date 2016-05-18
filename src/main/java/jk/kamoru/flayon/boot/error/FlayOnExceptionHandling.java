package jk.kamoru.flayon.boot.error;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class FlayOnExceptionHandling {

	@ExceptionHandler(value = FlayOnException.class)
	public ModelAndView exception(FlayOnException exception, WebRequest request) {
		ModelAndView modelAndView = new ModelAndView("error/flayonError");
		modelAndView.addObject("exception", exception);
		return modelAndView;
	}

}