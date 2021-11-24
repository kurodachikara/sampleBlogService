package jp.kuroda.sampleBlog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.Comment;
import jp.kuroda.sampleBlog.model.FileEntity;
import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.model.UserAccount;
import jp.kuroda.sampleBlog.model.UserAccountForm;
import jp.kuroda.sampleBlog.service.BlogService;
import jp.kuroda.sampleBlog.service.CommentService;
import jp.kuroda.sampleBlog.service.PersonService;
import jp.kuroda.sampleBlog.service.UploadFileService;
import jp.kuroda.sampleBlog.service.UserService;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {
	@Autowired
	private MockMvc mockMvc;
	

	@MockBean
	private PersonService personService;
	@MockBean
	private BlogService blogService;
	@MockBean
	private CommentService commentService;
	@MockBean
	private UserService userService;
	@MockBean
	private UploadFileService fileService;
	@MockBean
	private UserDetailsManager manager;
	
	private static UserAccount account;
	private static Map<String,Blog> blogMap;
	private static Blog blog;
	private static List<Blog> blogs;
	private static Map<String, Comment> commentMap;
	private static FileEntity fileEntity;
	@BeforeEach
	public void setUp() throws Exception{
		Person person1=new Person();
		person1.setName("chikara");
		
		account=new UserAccount();
		account.setUsername("hiroshi");
		account.setPassword("123456789");
		account.setPerson(person1);
		account.setType("user");
		when(userService.find("user")).thenReturn(account);
		
		Person person2=new Person();
		person2.setName("minato");
		
		blogMap=new HashMap<String,Blog>();
		
		blog=new Blog();
		blog.setTitle("Spring");
		blog.setContents("a");
		blog.setPerson(account.getPerson());
		blogMap.put("1", blog);
		
		Blog blog2=new Blog();
		blog2.setPerson(person2);
		blogMap.put("2", blog2);
		
		commentMap=new HashMap<String, Comment>();
		
		Comment comment1=new Comment();
		comment1.setBlog(blog);
		comment1.setPerson(person2);
		comment1.setCommenting("11");
		commentMap.put("1", comment1);
		
		Comment comment2=new Comment();
		comment2.setBlog(blog);
		comment2.setPerson(person1);
		comment2.setCommenting("22");
		commentMap.put("2", comment2);
		
		FileEntity fileEntity1=new FileEntity();
		fileEntity1.setBlog(blog2);
		
		fileEntity=new FileEntity();

		fileEntity.setBlog(blog);
		fileEntity.setFile(new MockMultipartFile("file", "test.text","text/plain","Spring Framework".getBytes()));
		
	}
	@TestConfiguration
	static class Config implements WebMvcConfigurer{

		@Override
		public void addFormatters(FormatterRegistry registry) {
			registry.addConverter(String.class,Blog.class,id->blogMap.get(id));
			registry.addConverter(String.class,Blog.class,id->blog);
			registry.addConverter(String.class,UserAccount.class,id->account);
			registry.addConverter(String.class,Person.class,id->account.getPerson());
			registry.addConverter(String.class,Comment.class,id->commentMap.get(id));
			registry.addConverter(String.class,FileEntity.class,id->fileEntity);
		}
	}
	@Test
	public void testIndexNotLogin() throws Exception{
		mockMvc.perform(get("/person/index"))
				.andExpect(status().isFound());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testIndexHasPermission() throws Exception{
		mockMvc.perform(get("/person/index"))
				.andExpect(status().isOk())
				.andExpect(view().name("person/index"))
				.andExpect(model().attribute("person", account.getPerson()));
	}
	@Test
	@WithMockUser(roles="USER")
	public void testSearch() throws Exception{
		when(blogService.getBlogList("Spring")).thenReturn(blogs);
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("word", "Spring");
		mockMvc.perform(get("/person/search").params(params))
				.andExpect(status().isOk())
				.andExpect(view().name("person/index"))
				.andExpect(model().attribute("blogs", blogs));
	}
	@SuppressWarnings("unchecked")
	@Test
	@WithMockUser(roles="USER")
	public void testEditProfileGet() throws Exception{
		when(blogService.getMyBlogs(account.getPerson())).thenReturn(blogs);
		MvcResult result=mockMvc.perform(get("/person/edit"))
								.andExpect(view().name("person/form"))
								.andReturn();
		List<Blog> blogs=(List<Blog>)result.getModelAndView().getModel().get("blogs");
		assertThat(blogs).isEqualTo(PersonControllerTest.blogs);
	}
	@Test
	@WithMockUser(roles="USER")
	public void testEditProfilePostFaile() throws Exception{
		MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
		params.add("name", "");
		mockMvc.perform(post("/person/edit").with(csrf()).params(params))
				.andExpect(view().name("person/form"))
				.andExpect(model().hasErrors());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testEditProfilePostSucccess() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		MockMultipartFile multipartFile=new MockMultipartFile("icon_file","test.txt","text/plain","Spring Framework".getBytes());
		params.add("name", "タイシ");
		params.add("birthday", "1990-12-12");
		params.add("hobby", "バレーボール");
		params.add("work", "飲食店");
		mockMvc.perform(multipart("/person/edit").file(multipartFile).with(csrf()).params(params))
				.andExpect(status().isFound());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testCreateBlogGet() throws Exception{
		mockMvc.perform(get("/person/create"))
				.andExpect(status().isOk())
				.andExpect(view().name("person/create"));
	}
	@Test
	@WithMockUser(roles="USER")
	public void testCreateBlogPostSuccess() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		MockMultipartFile file=new MockMultipartFile("file", "test.txt","text/plain","Spring Framework".getBytes());
		params.add("title", "Spring");
		params.add("contents", "a");
		mockMvc.perform(multipart("/person/create").file(file).with(csrf()).params(params))
				.andExpect(status().isFound());
	}
	
	@Test
	@WithMockUser(roles="USER")
	public void testEditBlogGet() throws Exception{
		mockMvc.perform(get("/person/blog/1/edit"))
				.andExpect(status().isOk())
				.andExpect(view().name("person/create"))
				.andExpect(model().attribute("blog", blog));
	}
	@Test
	@WithMockUser(roles="USER")
	public void testEditBlogPostFail() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		MockMultipartFile file=new MockMultipartFile("file", "test.txt","text/plain","Spring Framework".getBytes());
		params.add("title", "");
		mockMvc.perform(multipart("/person/blog/1/edit").file(file).with(csrf()).params(params))
				.andExpect(view().name("person/create"))
				.andExpect(model().hasErrors());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testEditBlogPostSuccess() throws Exception{
		MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
		MockMultipartFile file=new MockMultipartFile("file", "test.txt","text/plain","Spring Framework".getBytes());
		params.add("title", "Java");
		params.add("contents", "aaa");
		mockMvc.perform(multipart("/person/blog/1/edit").file(file).with(csrf()).params(params))
				.andExpect(status().isFound());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testDeleteGet() throws Exception{
		mockMvc.perform(get("/person/fileEntity/1/deleteimage"))
				.andExpect(status().isFound());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testDeleteMyBlog() throws Exception{
		mockMvc.perform(get("/person/blog/1/delete"))
				.andExpect(status().isFound());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testCommentBlog() throws Exception{
		MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
		params.add("blog", "1");
		params.add("person","1" );
		params.add("commenting", "a");
		mockMvc.perform(post("/person/comment/blog/1").with(csrf()).params(params))
				.andExpect(status().isFound());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testDeleteMyComment() throws Exception{
		mockMvc.perform(get("/person/comment/1/delete"))
				.andExpect(status().isFound());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testEditAccGet() throws Exception{
		MvcResult result=mockMvc.perform(get("/person/hiroshi/editPass"))
								.andExpect(status().isOk())
								.andExpect(view().name("person/editPass"))
								.andReturn();
		UserAccountForm form=(UserAccountForm)result.getModelAndView().getModel().get("userAccountForm");
		assertThat(form.getUsername()).isEqualTo(account.getUsername());
		assertThat(form.getType()).isEqualTo(account.getType());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testEditAccPostSuccess() throws Exception{
		MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
		params.add("password","123456789");
		params.add("username", "hiroshi");
		params.add("passwordConfirm", "123456789");
		mockMvc.perform(post("/person/hiroshi/editPass").with(csrf()).params(params))
				.andExpect(status().isFound());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testEditAccPostFail() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("password", "123");
		params.add("username", "hiroshi");
		params.add("passwordConfirm", "123");
		mockMvc.perform(post("/person/hiroshi/editPass").with(csrf()).params(params))
				.andExpect(view().name("person/editPass"))
				.andExpect(model().hasErrors());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testEditAccPostFailPassConfirm() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("password", "123456789");
		params.add("username", "hiroshi");
		params.add("passwordConfirm", "123123");
		mockMvc.perform(post("/person/hiroshi/editPass").with(csrf()).params(params))
				.andExpect(view().name("person/editPass"))
				.andExpect(model().hasErrors());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testDeletePersonInfo() throws Exception {
		mockMvc.perform(get("/person/fileEntity/1/deleteimage"))
		.andExpect(status().isFound());
	}
}
