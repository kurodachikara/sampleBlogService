package jp.kuroda.sampleBlog.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BlogTest {
	private Validator validator;
	
	@BeforeEach
	public void setUp() {
		ValidatorFactory factory=Validation.buildDefaultValidatorFactory();
		validator=factory.getValidator();
	}
	@Test
	public void testValidationSuccess() {
		Blog blog=new Blog();
		blog.setTitle("タイトル");
		blog.setContents("a");
		Set<ConstraintViolation<Blog>> violations=validator.validate(blog);
		assertThat(violations.size()).isEqualTo(0);
	}
	@Test
	public void testValidationFailTitleBlank() {
		Blog blog=new Blog();
		blog.setContents("a");
		Set<ConstraintViolation<Blog>> violations=validator.validate(blog);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<Blog>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	@Test
	public void testValidationFailContentBlank() {
		Blog blog=new Blog();
		blog.setTitle("タイトル");
		Set<ConstraintViolation<Blog>> violations=validator.validate(blog);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<Blog>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	@Test
	public void testValidationFailTitleSize() {
		Blog blog=new Blog();
		String s="";
		for(int i=0;i<12;i++) {
			s+="0123456789";
		}
		blog.setTitle(s);
		blog.setContents("a");
		Set<ConstraintViolation<Blog>> violations=validator.validate(blog);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<Blog>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
}
