package jp.kuroda.sampleBlog.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Entity
@Data
public class Person {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@NotBlank(message="ユーザー名を入力してください")
	@Size(max=80,message="ユーザー名は８０字以内で入力してください")
	@Column(length=80)
	private String name;
	
	@NotNull(message="誕生日を入力してください")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate birthday;
	
	@Size(max=100,message="100字以内で入力してください")
	@Column(length=100)
	private String hobby;
	
	@Size(max=100,message="職業は１００字以内で入力してください")
	@Column(length=100)
	private String work;
	
	private String icon_base64_str;
	
	@Transient
	private MultipartFile icon_file;
	
	@OneToMany(mappedBy="person")
	private List<Blog> blogs;
	@OneToMany(mappedBy="person")
	private List<Comment> comments;
	
}
