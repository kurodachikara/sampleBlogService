package jp.kuroda.sampleBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.kuroda.sampleBlog.model.Person;

public interface PersonRepository extends JpaRepository<Person, Integer>{
}
