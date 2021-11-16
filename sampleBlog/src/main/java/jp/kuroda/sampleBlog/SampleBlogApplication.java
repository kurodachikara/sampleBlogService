package jp.kuroda.sampleBlog;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DuplicateKeyException;

import jp.kuroda.sampleBlog.service.UserService;


@SpringBootApplication
public class SampleBlogApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SampleBlogApplication.class, args);
	}
	@Autowired(required=false)
	private UserService userService;
	@Override
	public void run(String... args) throws Exception {
		if(userService==null) {
			return;
		}
		String username="admin";
		String password="admin";
		String role="ADMIN";
		try {
			userService.createAdminAccount(username, password, role);
		}catch(DuplicateKeyException e){
			
		}
		
	}
	
}
