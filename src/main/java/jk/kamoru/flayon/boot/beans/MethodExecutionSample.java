package jk.kamoru.flayon.boot.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jk.kamoru.flayon.boot.security.User;
import jk.kamoru.flayon.boot.security.UserRepository;

@Component
public class MethodExecutionSample {

	@Autowired private UserRepository userRepository;

	public void loadInitData() {
		User user = new User();
		user.setName("kamoru");
		user.setPassword("crazyjk");
		user.setRole("USER");
		userRepository.save(user);
	}
}
