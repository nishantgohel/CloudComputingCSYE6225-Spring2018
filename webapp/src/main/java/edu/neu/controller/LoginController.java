package edu.neu.controller;

import javax.validation.Valid;

import edu.neu.model.User;
import edu.neu.service.S3Services;
import edu.neu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class LoginController {

	private String PROFILE_NAME;
	private String bucketName;

	@Autowired
	private UserService userService;

	@Autowired
    private Environment environment;

	@Autowired
    S3Services s3Services;

	@RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");

		return modelAndView;
	}


	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView registration(){
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		PROFILE_NAME = environment.getProperty("app.profile.name");
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult
					.rejectValue("email", "error.user",
							"There is already a user registered with the email provided");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			user.setPath("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQY-cYNxqLIgGM2GtDUUWlw0BFz9v_M8pl-YUXsfvVHFPmUAhMH");
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User has been registered successfully");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");

		}
		return modelAndView;
	}

	@RequestMapping(value="/home", method = RequestMethod.GET)
	public ModelAndView home(){
		PROFILE_NAME = environment.getProperty("app.profile.name");
		bucketName = System.getProperty("bucket.name");

		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName", "Welcome "  + user.getEmail());
		String time = LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"));
		modelAndView.addObject("userMessage", "Last Login "+time);
        modelAndView.addObject("aboutme", user.getAboutMe());
        
		// Download from bucket
		if(PROFILE_NAME.equals("aws") && 
		!("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQY-cYNxqLIgGM2GtDUUWlw0BFz9v_M8pl-YUXsfvVHFPmUAhMH").equals(user.getPath())){
			String keyName ="https://s3.amazonaws.com/"+
			bucketName+
			"/"+
			user.getPath();
			user.setPath(keyName);
		}

		if(user.getPath()== null){
			user.setPath("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQY-cYNxqLIgGM2GtDUUWlw0BFz9v_M8pl-YUXsfvVHFPmUAhMH");
		}

		modelAndView.addObject("picture", user.getPath());
		modelAndView.setViewName("home");
		return modelAndView;
	}


}
