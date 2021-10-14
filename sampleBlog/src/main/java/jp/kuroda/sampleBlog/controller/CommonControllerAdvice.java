package jp.kuroda.sampleBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.kuroda.sampleBlog.model.UserAccount;
import jp.kuroda.sampleBlog.service.UserService;

@ControllerAdvice
public class CommonControllerAdvice {
	@Autowired
	private UserService userService;
	
	@ModelAttribute("userAccount")
	public UserAccount currentAccount() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails)auth.getPrincipal();
            return userService.find(userDetails.getUsername());
        } else {
            return null;
        }
    }
}
