package jp.kuroda.sampleBlog.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.kuroda.sampleBlog.model.Blog;
import jp.kuroda.sampleBlog.model.Comment;
import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.repository.CommentRepository;

@Service
public class CommentService {
	@Autowired
	private CommentRepository commentRepository;
	
	public Comment getComment(Blog blog,Person person) {
		Comment comment=new Comment();
		comment.setBlog(blog);
		comment.setPerson(person);
		return comment;
	}
	public void createComment(Comment comment) {
		comment.setPostDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
		commentRepository.save(comment);
	}
	public void deleteComment(Comment comment) {
		commentRepository.deleteById(comment.getId());
	}
}
