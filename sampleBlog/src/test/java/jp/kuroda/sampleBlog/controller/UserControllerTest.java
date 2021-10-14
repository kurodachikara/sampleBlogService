package jp.kuroda.sampleBlog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.Comment;
import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.model.UserAccount;
import jp.kuroda.sampleBlog.service.BlogService;
import jp.kuroda.sampleBlog.service.CommentService;
import jp.kuroda.sampleBlog.service.PersonService;
import jp.kuroda.sampleBlog.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private BlogService blogService;
	@MockBean
	private PersonService personService;
	@MockBean
	private UserService userService;
	@MockBean
	private CommentService commentService;
	@MockBean
	private UserDetailsManager manager;
	
	private static List<Blog> blogs;
	private static Blog blog;
	private static UserAccount account;
	private static Map<String, Comment> commentMap;
	
	@BeforeEach
	public void setUp() {
		Person person=new Person();
		person.setName("chikara");
		account=new UserAccount();
		account.setPerson(person);
		account.setPassword("chikara");
		account.setUsername("chikara");
		when(userService.find("user")).thenReturn(account);
		
		Person person2=new Person();
		person2.setName("kuroda");
		
		blog=new Blog();
		blog.setTitle("Spring");
		blog.setContents("a");
		blog.setPerson(person);
		blogs=Arrays.asList(blog);
		
		commentMap=new HashMap<String,Comment>();
		
		Comment comment1=new Comment();
		comment1.setPerson(person2);
		comment1.setBlog(blog);
		comment1.setCommenting("a");
		commentMap.put("1", comment1);
		
		Comment comment2=new Comment();
		comment2.setBlog(blog);
		comment2.setPerson(person2);
		commentMap.put("2", comment2);
	}
	@TestConfiguration
	static class Config implements WebMvcConfigurer{

		@Override
		public void addFormatters(FormatterRegistry registry) {
			registry.addConverter(String.class,Blog.class,id->blog);
			registry.addConverter(String.class,Person.class,id->account.getPerson());
			registry.addConverter(String.class,Comment.class,id->commentMap.get(id));
		}
	}
	@Test
	public void testIndex() throws Exception{
		when(blogService.getBlogList()).thenReturn(blogs);
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andExpect(model().attribute("blogs", blogs));
	}
	@Test
	public void testSearch() throws Exception{
		when(blogService.getBlogList("Spring")).thenReturn(blogs);
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("word", "Spring");
		mockMvc.perform(get("/search").params(params))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andExpect(model().attribute("blogs", blogs));
	}
	@Test
	public void testProfile() throws Exception{
		mockMvc.perform(get("/profile/{personId}",1))
				.andExpect(status().isOk())
				.andExpect(view().name("profile"))
				.andExpect(model().attribute("person", account.getPerson()));
	}
	@Test
	public void testShowBlog() throws Exception{
		Person person=account.getPerson();
		Person person2=new Person();
		
		Comment comment=new Comment();
		comment.setPerson(person2);
		comment.setBlog(blog);
		MvcResult result=mockMvc.perform(get("/blog/{blogId}",1))
								.andExpect(status().isOk())
								.andExpect(view().name("blog"))
								.andReturn();
		Blog b=(Blog)result.getModelAndView().getModel().get("blog");
		assertThat(b.getPerson()).isEqualTo(person);
		when(commentService.getComment(blog, person2)).thenReturn(comment);
		assertThat(comment.getPerson()).isEqualTo(person2);
		assertThat(comment.getBlog()).isEqualTo(blog);
	}
	@Test
	public void testLogin() throws Exception{
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
	}
	@Test
	public void testLoginPost() throws Exception{
		MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
		params.add(account.getUsername(), account.getPassword());
		mockMvc.perform(post("/login").with(csrf()).params(params))
				.andExpect(status().isFound());
	}
	@Test
	public void testCreateUserAccountGet() throws Exception{
		mockMvc.perform(get("/createAccount"))
				.andExpect(view().name("createAccount"));
	}
	@Test
	public void testCreateUserAccountPostFile() throws Exception{
		MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
		mockMvc.perform(post("/createAccount").with(csrf()).params(params))
				.andExpect(view().name("createAccount"))
				.andExpect(model().hasErrors());
	}
	@Test
	public void testCreateUserAccountPostSuccess() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("username", "kuroda");
		params.add("password", "123456");
		params.add("type", "user");
		params.add("passwordConfirm", "123456");
		mockMvc.perform(post("/createAccount").with(csrf()).params(params))
				.andExpect(status().isFound());
	}
}
