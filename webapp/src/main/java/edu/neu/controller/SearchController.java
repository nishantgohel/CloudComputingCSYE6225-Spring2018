package edu.neu.controller;


import edu.neu.model.User;
import edu.neu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class SearchController {

    @Autowired
    private UserService userService;

    @RequestMapping(value={"/search"}, method = RequestMethod.GET)
    public ModelAndView goToSearchPage(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("search");
        return modelAndView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView searchUser(@ModelAttribute("user") User user, BindingResult bindingResult) {
        
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists == null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "No user with this email");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("search");
        } else {
            //modelAndView.addObject("user", userExists);
             if(userExists.getPath()== null){
                userExists.setPath("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQY-cYNxqLIgGM2GtDUUWlw0BFz9v_M8pl-YUXsfvVHFPmUAhMH");
            }
            modelAndView.addObject("aboutme", userExists.getAboutMe());
            modelAndView.addObject("picture", userExists.getPath());
            modelAndView.addObject("userName", "User: "  + userExists.getEmail());
            String time = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"));
            modelAndView.addObject("userMessage", "Last Login "+time);
            
            modelAndView.setViewName("searchedUser");

        }
        return modelAndView;
    }

}
