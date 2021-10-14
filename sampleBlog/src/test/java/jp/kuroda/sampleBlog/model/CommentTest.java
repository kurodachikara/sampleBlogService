package jp.kuroda.sampleBlog.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommentTest {
	private Validator validator;
	
	@BeforeEach
	public void setUp() {
		ValidatorFactory factory=Validation.buildDefaultValidatorFactory();
		validator=factory.getValidator();
	}
	@Test
	public void testValidationSuccess() {
		Comment comment=new Comment();
		comment.setCommenting("a");
		Set<ConstraintViolation<Comment>> violations=validator.validate(comment);
		assertThat(violations.size()).isEqualTo(0);
	}
	@Test
	public void testValidationFailNameBlank() {
		Comment comment=new Comment();
		String s="";
		for(int i=0;i<110;i++) {
			s+="0123456789";
		}
		comment.setCommenting(s);
		Set<ConstraintViolation<Comment>> violations=validator.validate(comment);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<Comment>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
}
