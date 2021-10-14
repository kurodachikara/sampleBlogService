package jp.kuroda.sampleBlog.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.repository.BlogRepository;

@Service
public class BlogService {
	@Autowired
	private BlogRepository blogRepository;
	
	
	public List<Blog> getBlogList(){
		return blogRepository.findAll();
	}
	public List<Blog> getMyBlogs(Person person){
		return blogRepository.findByPerson(person);
	}
	public List<Blog> getBlogList(String word){
		return blogRepository.findByTitleContains(word);
	}
	
	public void createBlog(Blog blog){
		blog.setPostDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
		blogRepository.save(blog);
	}
	public void editBlog(Blog blog) {
		blog.setPostDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
		blogRepository.save(blog);
	}
	public void deleteBlog(Blog blog) {
		blogRepository.deleteById(blog.getId());
	}
	public void deleteImage(Blog blog) {
		blog.setFile(null);
		blog.setImage_byte(null);
		blog.setBase64_str(null);
		blogRepository.save(blog);
	}
}
