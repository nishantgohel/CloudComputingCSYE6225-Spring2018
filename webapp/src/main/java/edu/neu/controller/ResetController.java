package edu.neu.controller;

import edu.neu.model.User;
import edu.neu.service.UserService;
import edu.neu.service.SNSMessage;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.core.env.Environment;

import com.amazonaws.services.sns.*;
import com.amazonaws.auth.*;
import com.amazonaws.regions.*;
import com.amazonaws.services.sns.model.*;

@Controller
public class ResetController {

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @RequestMapping(value={"/reset"}, method = RequestMethod.GET)
    public ModelAndView goToResetPage(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
		modelAndView.addObject("user", user);
        modelAndView.setViewName("reset");
        return modelAndView;
    }

    @RequestMapping(value={"/reset"}, method = RequestMethod.POST)
    public ModelAndView resetPassword(@ModelAttribute("user") User user, BindingResult bindingResult){
        ModelAndView modelAndView = new ModelAndView();

        try{
            String email = user.getEmail();
            String domain = System.getProperty("domain.name");

            modelAndView.setViewName("reset");
            modelAndView.addObject("ResetMessage", "Password reset mail has been sent");

            String messageBody = "Hi "+email+
            "\n\nWe received a request for password reset. Please click on the following link to reset your password.\n\n"+
            "http://"+
                    domain+
                    "/reset?email="+
                    email+
                    "&token=";

            AmazonSNSClient snsClient = new AmazonSNSClient(new DefaultAWSCredentialsProviderChain());
            snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));

            List<Topic> topicArns = new ArrayList<>();

            ListTopicsResult result = snsClient.listTopics();
            topicArns.addAll(result.getTopics());

            for (Topic topic : topicArns) {
                if(topic.getTopicArn().endsWith("PasswordResetSNSTopic")){

                    // Initialize example message class
                    SNSMessage message = new SNSMessage(messageBody);

                    // Add message attribute with string value
                    message.addAttribute("to_email", email);
                    message.addAttribute("from_email", "reset_password@"+domain);

                    // Publish message
                    message.publish(snsClient, topic.getTopicArn());

                    break;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            return modelAndView;
        }

    }

}
