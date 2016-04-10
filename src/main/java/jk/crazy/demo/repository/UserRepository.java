package jk.crazy.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jk.crazy.demo.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findByName(String name);

	List<User> findByNameLike(String name);
	
}
