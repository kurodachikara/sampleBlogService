package jp.kuroda.sampleBlog.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.kuroda.sampleBlog.model.Comment;
import jp.kuroda.sampleBlog.model.Person;

public interface CommentRepository extends JpaRepository<Comment, Integer>{
	List<Comment> findByPerson(Person person);
}