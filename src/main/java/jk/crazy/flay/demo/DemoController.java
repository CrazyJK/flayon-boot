package jk.crazy.flay.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/demo")
@Slf4j
public class DemoController {

	@Autowired private UserRepository userRepository;

	@RequestMapping
    public @ResponseBody String index() {
		log.debug("call");
        return "Hello crazy Spring Boot~";
    }
	
	@RequestMapping("/users/{name}")
	public String getUserList(Model model, @PathVariable String name) {
		log.debug("call {}", name);
		if (StringUtils.isEmpty(name) || "all".equalsIgnoreCase(name))
			model.addAttribute(userRepository.findAll());
		else
			model.addAttribute(userRepository.findByNameLike(name + "%"));
		return "users";
	}
	
	@RequestMapping("/users/add")
	public String addUserList() {
		log.debug("call add user");
		User user = new User();
		user.setName("kamoru" + Double.valueOf(Math.random() * 100).intValue());
		user.setAge(Double.valueOf(Math.random() * 100).intValue());
		
		userRepository.save(user);
		
		return "redirect:/demo/users/all";
	}
	
	
}
