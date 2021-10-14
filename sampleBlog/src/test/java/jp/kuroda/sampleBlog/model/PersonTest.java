package jp.kuroda.sampleBlog.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersonTest {
	private Validator validator;
	
	@BeforeEach
	public void setUp() {
		ValidatorFactory factory=Validation.buildDefaultValidatorFactory();
		validator=factory.getValidator();
	}
	@Test
	public void testValidationSuccess() {
		Person person=new Person();
		person.setName("chikara");
		person.setBirthday(LocalDate.of(1994, 6, 4));
		person.setHobby("バレーボール");
		person.setWork("コック");
		Set<ConstraintViolation<Person>> violations=validator.validate(person);
		assertThat(violations.size()).isEqualTo(0);
	}
	@Test
	public void testValidationFailNameBlank() {
		Person person=new Person();
		person.setBirthday(LocalDate.of(1994, 6, 4));
		person.setHobby("バレーボール");
		person.setWork("コック");
		Set<ConstraintViolation<Person>> violations=validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<Person>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	@Test
	public void testValidationFailBirthBlank() {
		Person person=new Person();
		person.setName("chikara");
		person.setHobby("バレーボール");
		person.setWork("コック");
		Set<ConstraintViolation<Person>> violations=validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<Person>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotNull.class);
		}
	}
	@Test
	public void testValidationFailNameSize() {
		Person person=new Person();
		String s="";
		for(int i=0;i<10;i++) {
			s+="0123456789";
		}
		person.setName(s);
		person.setBirthday(LocalDate.of(1994, 6, 4));
		person.setHobby("バレーボール");
		person.setWork("コック");
		Set<ConstraintViolation<Person>> violations=validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<Person>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
	@Test
	public void testValidationFailHobbySize() {
		Person person=new Person();
		String s="";
		for(int i=0;i<12;i++) {
			s+="0123456789";
		}
		person.setName("chikara");
		person.setBirthday(LocalDate.of(1994, 6, 4));
		person.setHobby(s);
		person.setWork("コック");
		Set<ConstraintViolation<Person>> violations=validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<Person>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
	@Test
	public void testValidationFailWorkSize() {
		Person person=new Person();
		String s="";
		for(int i=0;i<12;i++) {
			s+="0123456789";
		}
		person.setName("chikara");
		person.setBirthday(LocalDate.of(1994, 6, 4));
		person.setHobby("バレーボール");
		person.setWork(s);
		Set<ConstraintViolation<Person>> violations=validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for(ConstraintViolation<Person>violation:violations) {
			Object annotation=violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
}
