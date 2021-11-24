package jp.kuroda.sampleBlog.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.kuroda.sampleBlog.model.Account;
import jp.kuroda.sampleBlog.model.UserAccount;
import jp.kuroda.sampleBlog.service.UserService;

@RequestMapping("/account")
@Controller
public class AccountController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("")
	public String index(Model model) {
		List<UserAccount> userAccounts=userService.getAccountList();
		model.addAttribute("userAccounts",userAccounts);
		return "account/index";
	}
	@GetMapping("/delete/{username}")
	public String deleteAccount(@PathVariable("username")UserAccount userAccount) {
		userService.deleteUser(userAccount);
		return"redirect:/account";
	}
	
	@GetMapping("/{accountId}/edit")
	public String editAdminAccount(@PathVariable("accountId")Account account,Model model) {
		model.addAttribute("account",account);
		return"account/edit";
	}
	@PostMapping("{accountId}/edit")
	public String editAdminAccount(@PathVariable("accountId")Account account,@RequestParam String username,@RequestParam String password) {
		userService.updateAdminAccount(username, password);
		return"redirect:/account";
	}
	
	@GetMapping("/createAdmin")
	public String createAdmin() {
		return "account/create";
	}
	@PostMapping("/createAdmin")
	public String createAdmin(String username,String password) {
		userService.createAdminAccount(username, password, "ADMIN");
		return"redirect:/account";
	}
}
