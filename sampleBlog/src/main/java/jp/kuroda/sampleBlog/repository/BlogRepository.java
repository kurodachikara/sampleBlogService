package jp.kuroda.sampleBlog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.Person;

public interface BlogRepository extends JpaRepository<Blog, Integer>{
	List<Blog> findByTitleContains(String word);
	List<Blog> findByPerson(Person person);
}
