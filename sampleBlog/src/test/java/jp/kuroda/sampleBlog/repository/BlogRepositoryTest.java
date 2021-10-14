package jp.kuroda.sampleBlog.repository;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jp.kuroda.sampleBlog.model.Blog;

@DataJpaTest

public class BlogRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private BlogRepository blogRepository;
	
	@BeforeEach
	public void setUp() {
		Blog blog1=new Blog();
		blog1.setTitle("Spring");
		blog1.setContents("aaa");
		entityManager.persist(blog1);
		
		Blog blog2=new Blog();
		blog2.setTitle("java");
		blog2.setContents("bb");
		entityManager.persist(blog2);
	}
	
	@Test
	public void testFindByTitleContains() {
		List<Blog> blog=blogRepository.findByTitleContains("Spring");
		assertThat(blog.size()).isEqualTo(1);
	}
}
