package jp.kuroda.sampleBlog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.Comment;
import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.repository.CommentRepository;

@SpringJUnitConfig
public class CommentServiceTest {
	@TestConfiguration
	static class Config{
		@Bean
		public CommentService commentService() {
			return new CommentService();
		}
	}
	@Autowired
	private CommentService commentService;
	
	@MockBean
	private CommentRepository commentRepository;
	
	@Test
	public void testGetComment() {
		Person person=new Person();
		Blog blog=new Blog();
		Comment comment=commentService.getComment(blog, person);
		assertThat(comment.getBlog()).isEqualTo(blog);
		assertThat(comment.getPerson()).isEqualTo(person);
	}
	@Test
	public void testCreateComment() {
		Comment comment=new Comment();
		commentService.createComment(comment);
		verify(commentRepository).save(comment);
	}
	@Test
	public void testDeleteComment() {
		Comment comment=new Comment();
		commentService.deleteComment(comment);
		verify(commentRepository).deleteById(comment.getId());
	}
}
