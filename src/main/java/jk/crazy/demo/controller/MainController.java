package jk.crazy.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jk.crazy.demo.domain.User;
import jk.crazy.demo.repository.UserRepository;

@Controller
@RequestMapping("/")
public class MainController {

	@Autowired private UserRepository userRepository;

	@RequestMapping
    public @ResponseBody String index() {
        return "Hello Woniper Spring Boot~";
    }
	
	@RequestMapping("/users/{name}")
	public String getUserList(Model model, @PathVariable String name) {
		if (StringUtils.isEmpty(name) || "all".equalsIgnoreCase(name))
			model.addAttribute(userRepository.findAll());
		else
			model.addAttribute(userRepository.findByNameLike(name + "%"));
		return "users";
	}
	
	@RequestMapping("/addusers")
	public @ResponseBody List<User> addUserList() {
		User user = new User();
		user.setName("kamoru" + Double.valueOf(Math.random() * 100).intValue());
		user.setAge(Double.valueOf(Math.random() * 100).intValue());
		
		userRepository.save(user);
		
		return userRepository.findByNameLike("kamoru%");
	}
	
	
}
