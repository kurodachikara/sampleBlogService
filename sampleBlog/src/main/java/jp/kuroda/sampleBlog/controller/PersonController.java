package jp.kuroda.sampleBlog.controller;


import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.Comment;
import jp.kuroda.sampleBlog.model.FileEntity;
import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.model.UserAccount;
import jp.kuroda.sampleBlog.model.UserAccountForm;
import jp.kuroda.sampleBlog.service.BlogService;
import jp.kuroda.sampleBlog.service.PersonService;
import jp.kuroda.sampleBlog.service.UploadFileService;
import jp.kuroda.sampleBlog.service.UserService;
import jp.kuroda.sampleBlog.service.CommentService;

@Controller
@RequestMapping("/person")
public class PersonController {
	@Autowired
	private PersonService personService;
	@Autowired
	private BlogService blogService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private UserService userService;
	@Autowired
	private UploadFileService fileService;

	
	@ModelAttribute("person")
	public Person currentPerson(UserAccount userAccount) {
		return userAccount.getPerson();
	}
	
	//ログイントップ画面
	@GetMapping("/index")
	public String index(Model model) {
		List<Blog> blogs=blogService.getBlogList();
		model.addAttribute("blogs",blogs);
		return "person/index";
	}
	//記事検索
	@GetMapping("/search")
	public String search(@RequestParam String word,Model model) {
		List<Blog> blogs=blogService.getBlogList(word);
		model.addAttribute("blogs",blogs);
		return"person/index";
	}
	
	//プロフィール編集
	@GetMapping("/edit")
	public String edit(Person person,Model model){
		List<Blog> blogs=blogService.getMyBlogs(person);
		model.addAttribute("blogs",blogs);
		model.addAttribute("base64icon",person.getIcon_base64_str());
		return"person/form";
	}
	@PostMapping("/edit")
	public String edit(@Valid Person person,BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return"person/form";
		}
		if(!person.getIcon_file().isEmpty()) {
			try {
				MultipartFile file=person.getIcon_file();
				StringBuffer data=new StringBuffer();
				String base64=new String(Base64.encodeBase64(file.getBytes()),"ASCII");
				data.append("data:image/jpeg;base64,");
				data.append(base64);
				person.setIcon_base64_str(data.toString());
			}catch(Exception e) {
			}
		}
		personService.updatePerson(person);
		return"redirect:/person/index";
	}
	
	//ブログ投稿
	@GetMapping("/create")
	public String createBlog(Blog blog,Person person) {
		
		blog.setPerson(person);
		return"person/create";
	}
	@PostMapping("/create")
	public String createBlog(@RequestParam("mfiles")List<MultipartFile> mfiles,@Valid Blog blog,BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "person/create";
		}
		blogService.createBlog(blog);
		fileService.saveFile(mfiles, blog);
		return"redirect:/person/blog/"+blog.getId();
	}
	//ブログ閲覧
	@GetMapping("/blog/{blogId}")
	public String showBlog(@PathVariable("blogId") Blog blog,Person person,FileEntity fileEntity,Model model){
	    
		model.addAttribute("blog",blog);
		Comment comment=commentService.getComment(blog,person);
		model.addAttribute("comment",comment);
		return"person/blog";
	}
	
	//ブログ編集
	@GetMapping("/blog/{blogId}/edit")
	public String editBlog(@PathVariable("blogId")Blog blog,FileEntity file,Model model) {
		checkBlogOwner(blog,model);
		
		model.addAttribute("blog",blog);
		return"person/create";
	}
	@PostMapping("/blog/{blogId}/edit")
	public String editBlog(@RequestParam("mfiles")List<MultipartFile> mfiles,@Valid Blog blog,BindingResult bindingResult,Model model) {
		if(bindingResult.hasErrors()) {
			return"person/create";
		}
		blogService.editBlog(blog);
		fileService.saveFile(mfiles, blog);
		model.addAttribute("blog", blog);
		return"redirect:/person/edit";
	}
	
	//画像削除＠create.html
	@Transactional
	@GetMapping("/fileEntity/{id}/deleteimage")
	public String delete(@PathVariable("id")FileEntity file) {
		fileService.deleteImage(file);;
		return"redirect:/person/blog/"+file.getBlog().getId()+"/edit";
	}
	
	//ブログ削除
	@Transactional
	@GetMapping("/blog/{id}/delete")
	public String deleteMyBlog(Blog blog) {
		blogService.deleteBlog(blog);
		return"redirect:/person/index";
	}
	//コメント投稿
	@PostMapping("/comment/blog/{blogId}")
	public String commentBlog(Comment comment) {
		commentService.createComment(comment);
		return"redirect:/person/blog/"+comment.getBlog().getId();
	}
	//コメント削除
	@Transactional
	@GetMapping("/comment/{id}/delete")
	public String deleteMyComment(Comment comment) {
		commentService.deleteComment(comment);
		return"redirect:/person/index";
	}
	
	//アカウント情報編集
	@GetMapping("/{username}/editPass")
	public String editAcc(@PathVariable("username") UserAccount account,UserAccountForm accountForm,Model model) {
		checkPersonOwner(account,model);
		
		accountForm.setType(account.getType());
		accountForm.setUsername(account.getUsername());
		return "person/editPass";
	}
	@PostMapping("/{username}/editPass")
	public String editAcc(@PathVariable("username") UserAccount account,String passwordConfirm,@Valid UserAccountForm accountForm,BindingResult result) {
		if(!accountForm.getPassword().equals("")&&result.hasErrors()) {
			return"person/editPass";
		}
		if(!passwordConfirm.equals(accountForm.getPassword())) {
			result.addError(new FieldError("accountForm","password","パスワードが一致しません。もう一度入力してください。"));
			return"person/editPass";
		}else {
		userService.updateUserAccount(accountForm.getUsername(), accountForm.getPassword());
		return"redirect:/person/index";
		}
	}
	
	//アカウント削除
	@GetMapping("/deleteUserAccount")
	public String deletePersonInfo(Person person) {
		personService.deletePersonInfomation(person);
		return"redirect:/person/index";
	}
	@ResponseStatus(HttpStatus.FORBIDDEN)
	private class ForbiddenPersonAccessException extends RuntimeException{
		private static final long serialVersionUID=1L;
		public ForbiddenPersonAccessException(String message) {
			super(message);
		}
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	private class PersonNotFoundException extends RuntimeException{
		private static final long serialVersionUID=1L;
		public PersonNotFoundException(String message) {
			super(message);
		}
	}
	private void checkPersonOwner(UserAccount account,Model model) {
		if(account==null) {
			throw new PersonNotFoundException("Not Found");
		}
		Map<String, Object> map=model.asMap();
		Person person=(Person)map.get("person");
		if(!person.equals(account.getPerson())) {
			throw new ForbiddenPersonAccessException("Forbidden");
		}
	}
	private void checkBlogOwner(Blog blog,Model model) {
		if(blog==null) {
			throw new PersonNotFoundException("Not Found");
		}
		Map<String,Object> map=model.asMap();
		Person person=(Person)map.get("person");
		if(!person.equals(blog.getPerson())) {
			throw new ForbiddenPersonAccessException("Forbidden");
		}
	}
}
