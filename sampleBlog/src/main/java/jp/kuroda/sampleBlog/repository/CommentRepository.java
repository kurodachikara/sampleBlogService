package jp.kuroda.sampleBlog.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import jp.kuroda.sampleBlog.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer>{
}
