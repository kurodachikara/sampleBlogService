package jp.kuroda.sampleBlog.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.repository.PersonRepository;

@Service
public class PersonService {
	@Autowired
	private PersonRepository personRepository;
	
	public Person createPerson() {
		Person person=new Person();
		person.setName("名前を設定してください");
		person.setBirthday(LocalDate.of(2021, 1, 1));
		person.setHobby("趣味・特技を設定してください");
		person.setWork("職業を設定してください");
		personRepository.save(person);
		return person;
	}
	public void updatePerson(Person person) {
	personRepository.save(person);
	}
}
