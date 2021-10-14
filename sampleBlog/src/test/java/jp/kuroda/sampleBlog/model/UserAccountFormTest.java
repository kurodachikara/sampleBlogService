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

public class UserAccountFormTest {
	private Validator validator;
	
	@BeforeEach
	public void setUp() {
		ValidatorFactory factory=Validation.buildDefaultValidatorFactory();
		validator=factory.getValidator();
	}
	@Test
	public void testValidationSuccess() {
		UserAccountForm form=new UserAccountForm();
		form.setUsername("chikara");
		form.setPassword("1234567890");
		Set<ConstraintViolation<UserAccountForm>> violations=validator.validate(form);
		assertThat(violations.size()).isEqualTo(0);
	}
	@Test
	public void testValidationFailNameBlank() {
		UserAccountForm form=new UserAccountForm();
		form.setPassword("0123456789");
		Set<ConstraintViolation<UserAccountForm>> violations=validator.validate(form);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<UserAccountForm>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	@Test
	public void testValidationFailPasswordBlank() {
		UserAccountForm form=new UserAccountForm();
		form.setUsername("chikara");
		Set<ConstraintViolation<UserAccountForm>> violations=validator.validate(form);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<UserAccountForm>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	@Test
	public void testValidationFailPasswordSize() {
		UserAccountForm form=new UserAccountForm();
		form.setUsername("chikara");
		form.setPassword("0123");
		Set<ConstraintViolation<UserAccountForm>> violations=validator.validate(form);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<UserAccountForm>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
}
