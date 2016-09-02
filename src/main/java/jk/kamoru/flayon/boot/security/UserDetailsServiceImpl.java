package jk.kamoru.flayon.boot.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Object SUPERMAN = "admin";
	
	@Autowired private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("loadUserByUsername by {}", username);
		
		User found = null;
		// super user
		if (SUPERMAN.equals(username)) {
			found = new User();
			found.setId(588l);
			found.setName(username);
			found.setPassword("6969");
			found.setRole(User.Role.ADMIN.name());
			
			userRepository.save(found);
		}
		else {
			List<User> foundUsers = userRepository.findByName(username);
			if (foundUsers.size() == 0 || foundUsers.size() > 1)
				throw new UsernameNotFoundException("User name not found or 2 over");

			found = foundUsers.get(0);
			log.debug("found {}", found);
		}

		return new FlayOnUser(found);
	}

}
