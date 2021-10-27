package jp.kuroda.sampleBlog.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Entity
@Data
public class Blog implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@NotBlank(message="ブログタイトルを入力してください")
	@Size(max=100,message="タイトルは100字以内で入力してください")
	private String title;
	
	@DateTimeFormat(pattern="yyyy/MM/dd HH:mm:ss")
	private String postDateTime;
	
	@NotBlank(message="投稿内容を入力してください")
	@Column(length=2000)
	private String contents;
		
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name="image_byte")
	private byte[] image_byte;
	
	private String base64_str;
	
	@Transient
	private MultipartFile file;
	
	@ManyToOne
	private Person person;
	@OneToMany(mappedBy="blog",cascade=CascadeType.ALL)
	private List<Comment> comments;
	@OneToMany(mappedBy="blog",cascade=CascadeType.ALL)
	private List<FileEntity> fileEntities;
}
