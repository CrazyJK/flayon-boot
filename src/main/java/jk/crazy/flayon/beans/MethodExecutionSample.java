package jk.crazy.flayon.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jk.crazy.flayon.security.User;
import jk.crazy.flayon.security.UserRepository;

@Component
public class MethodExecutionSample {

	@Autowired private UserRepository userRepository;

	public void loadInitData() {
		User user = new User();
		user.setName("kamoru" + Double.valueOf(Math.random() * 100).intValue());
		user.setPassword(String.valueOf(Double.valueOf(Math.random() * 100).intValue()));
		user.setRole("USER");
		userRepository.save(user);
	}
}
