package jp.kuroda.sampleBlog.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.Comment;
import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.model.UserAccountForm;
import jp.kuroda.sampleBlog.service.BlogService;
import jp.kuroda.sampleBlog.service.CommentService;
import jp.kuroda.sampleBlog.service.UserService;

@Controller
public class UserController {
	@Autowired
	private BlogService blogService;
	@Autowired
	private UserService userService;
	@Autowired
	private CommentService commentService;
	
	
	@GetMapping("/")
	public String index(Model model) {
		List<Blog> blogs=blogService.getBlogList();
		model.addAttribute("blogs", blogs);
		return "index";
	}
	@GetMapping("/search")
	public String search(@RequestParam String word,Model model) {
		List<Blog> blogs=blogService.getBlogList(word);
		model.addAttribute("blogs",blogs);
		return"index";
	}
	@GetMapping("/profile/{personId}")
	public String profile(@PathVariable("personId")Person person,Model model) {
		model.addAttribute("person",person);
		return"profile";
	}
	@GetMapping("/blog/{blogId}")
	public String showBlog(@PathVariable("blogId") Blog blog,Person person,Model model) {
		model.addAttribute("base64image",blog.getBase64_str());
		model.addAttribute("blog",blog);
		Comment comment=commentService.getComment(blog,person);
		model.addAttribute("comment",comment);
		return"blog";
	}
	
	@GetMapping("/login")
	public String login() {
		return"login";
	}
	
	
	@GetMapping("/createAccount")
	public String createUserAccount(UserAccountForm userAccountForm) {
		return"createAccount";
	}
	@PostMapping("/createAccount")
	public String createUserAccount(String passwordConfirm,@Valid UserAccountForm userAccountForm,BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return"createAccount";
		}
		if(!passwordConfirm.equals(userAccountForm.getPassword())) {
			bindingResult.addError(new FieldError("accountForm","password","パスワードが一致しません。もう一度入力してください。"));
			return"createAccount";
		}else {
			try{
				userService.createUserAccount(userAccountForm.getUsername(),userAccountForm.getPassword(), "USER");
				return"redirect:/person/index";
				}catch(DuplicateKeyException e) {
					bindingResult.addError(new FieldError("accountForm","username","既に存在するユーザーIDです"));
					return"createAccount";
				}
		}
	}
}
