package jp.kuroda.sampleBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.kuroda.sampleBlog.model.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Integer>{

}
