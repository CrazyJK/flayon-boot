package jk.crazy.flayon.security;

import java.util.Arrays;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class FlayOnUser extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = -167697137631555257L;

	private User user;

	public FlayOnUser(User user) {
		super(user.getName(), user.getPassword(), Arrays.asList(new SimpleGrantedAuthority(user.getRole())));
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

}
