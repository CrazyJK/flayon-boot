package jk.crazy.flay.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("call by {}", username);
		List<jk.crazy.flay.security.User> foundUsers = userRepository.findByName(username);
		if (foundUsers.size() == 0 || foundUsers.size() > 1)
			throw new UsernameNotFoundException("User bane not found or 2 over");

		log.debug("found {}", foundUsers.get(0));

		return new User(username, foundUsers.get(0).getPassword(), Arrays.asList(new SimpleGrantedAuthority("USER")));
	}

}
