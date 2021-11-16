package jp.kuroda.sampleBlog.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data

public class UserAccount {
	@Id
	@Column(length=80)
	private String username;
	
	@Column(length=80)
	private String password;
		
	private String type;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Person person;
	@OneToOne(cascade=CascadeType.ALL)
	private Account account;
}
