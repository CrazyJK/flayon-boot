package jk.crazy.flay.security;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
	
	private static final String ListView = "user/list";
	private static final String DetailView = "user/user";
	private static final String Redirect_List = "redirect:/user";
	
	@Autowired private UserRepository userRepository;

	@RequestMapping
    public String list(Model model) {
		log.debug("list");
		model.addAttribute(userRepository.findAll());
		return ListView;
    }

	@RequestMapping(method=RequestMethod.POST)
	public String postUser(@Valid User user, BindingResult result) {
		log.debug("save user {}", user);
		if (result.hasErrors()) {
			log.debug("save user : valid error. {}", result);
			return ListView;
		}
		userRepository.save(user);
		return Redirect_List;
	}

	@RequestMapping(method=RequestMethod.PUT)
	public User putUser(@Valid User user, BindingResult result) {
		log.debug("save user {}", user);
		if (result.hasErrors()) {
			log.debug("save user : valid error. {}", result);
			return null;
		}
		return userRepository.save(user);
	}

	@RequestMapping(method=RequestMethod.DELETE)
	public String deleteUser(@ModelAttribute User user) {
		log.debug("delete user {}", user);
		userRepository.delete(user);
		return Redirect_List;
	}


	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public String getUserList(Model model, @PathVariable Long id) {
		log.debug("call {}", id);
		model.addAttribute(userRepository.findOne(id));
		return DetailView;
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.POST)
	public String editUser(@Valid User user, BindingResult result, @PathVariable Long id) {
		log.debug("edit user {}", user);
		if (result.hasErrors()) {
			log.debug("edit user : valid error. {}", result);
			return DetailView;
		}
		userRepository.save(user);
		return Redirect_List;
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public String deleteUser(@PathVariable Long id) {
		log.debug("delete user id = {}", id);
		userRepository.delete(id);
		return Redirect_List;
	}
	
	
	@RequestMapping("/add")
	public String addUserList() {
		log.debug("call add user");
		User user = new User();
		user.setName("kamoru" + Double.valueOf(Math.random() * 100).intValue());
		user.setPassword(String.valueOf(Math.random() * 100));
		
		userRepository.save(user);
		
		return Redirect_List;
	}
		
}
