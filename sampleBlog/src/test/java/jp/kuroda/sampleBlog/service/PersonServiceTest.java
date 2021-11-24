package jp.kuroda.sampleBlog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.repository.BlogRepository;
import jp.kuroda.sampleBlog.repository.CommentRepository;
import jp.kuroda.sampleBlog.repository.PersonRepository;

@SpringJUnitConfig
public class PersonServiceTest {
	@TestConfiguration
	static class Congig{
		@Bean
		public PersonService personService() {
			return new PersonService();
		}
	}
	@Autowired
	private PersonService personService;
	
	@MockBean
	private PersonRepository personRepository;
	@MockBean
	private BlogService blogService;
	@MockBean
	private CommentService commentService;
	@MockBean
	private BlogRepository blogRepository;
	@MockBean
	private CommentRepository commentRepository;
	
	@Test
	public void testCreatePerson() {
		Person person=personService.createPerson();
		assertThat(person.getName()).isNotBlank();
		assertThat(person.getBirthday()).isNotNull();
		verify(personRepository).save(person);
	}
	@Test
	public void testUpdatePerson() {
		Person person=new Person();
		personService.updatePerson(person);
		verify(personRepository).save(person);
	}
	@Test
	public void testDeletePersonInfomation() {
		Person person=new Person();
		personService.deletePersonInfomation(person);
		verify(blogRepository).deleteAll(blogService.getMyBlogs(person));
		verify(commentRepository).deleteAll(commentService.getMyComment(person));
		verify(personRepository).save(person);
	}
}
