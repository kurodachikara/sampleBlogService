package jp.kuroda.sampleBlog.service;

import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.repository.BlogRepository;

@SpringJUnitConfig
public class BlogServiceTest {
	@TestConfiguration
	static class Config{
		@Bean
		public BlogService blogService() {
			return new BlogService();
		}
	}
	@Autowired
	private BlogService blogService;
	
	@MockBean
	private BlogRepository blogRepository;
	
	@Test
	public void testGetBlogList() {
		List<Blog> blog=blogService.getBlogList();
		verify(blogRepository).findAll();
	}
	
	@Test
	public void testGetMyBlogs() {
		Person person=new Person();
		List<Blog> blog=blogService.getMyBlogs(person);
		verify(blogRepository).findByPerson(person);
	}
	@Test
	public void testGetBlogsList() {
		String word="Spring";
		List<Blog> blog=blogService.getBlogList(word);
		verify(blogRepository).findByTitleContains(word);
	}
	@Test
	public void testCreateBlog() {
		Blog blog1=new Blog();
		blogService.createBlog(blog1);
		verify(blogRepository).save(blog1);
	}
	@Test
	public void testEditBlog() {
		Blog blog=new Blog();
		blogService.editBlog(blog);
		verify(blogRepository).save(blog);
	}
	@Test
	public void testDeleteBlog() {
		Blog blog=new Blog();
		blogService.deleteBlog(blog);
		verify(blogRepository).deleteById(blog.getId());
	}
	/*@Test
	public void testDeleteImage() {
		Blog blog=new Blog();
		blogService.deleteImage(blog);
		verify(blogRepository).save(blog);
	}*/
}
