package jp.kuroda.sampleBlog.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.model.UserAccount;
import jp.kuroda.sampleBlog.repository.UserAccountRepository;

@Service
public class UserService {
	@Autowired
	private UserDetailsManager userDetailsManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserAccountRepository accountRepository;
	@Autowired
	private PersonService personService;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	public UserAccount find(String username) {
		return accountRepository.findById(username).get();
	}
	
	@Transactional
	public UserAccount createUserAccount(String username,String password,String role) {
		UserBuilder builder=User.builder();
		UserDetails userDetails=builder.username(username)
										.password(passwordEncoder.encode(password))
										.roles(role)
										.build();
		userDetailsManager.createUser(userDetails);
		Person person=personService.createPerson();
		UserAccount account=new UserAccount();
		account.setUsername(username);
		account.setPassword(passwordEncoder.encode(password));
		account.setType("user");
		account.setPerson(person);
		accountRepository.save(account);
		return account;
	}
	@Transactional
	public void updateUserAccount(String username,String password) {
		UserDetails userDetails=userDetailsManager.loadUserByUsername(username);
		UserBuilder builder;
		if(!password.equals("")) {
			builder=User.builder();
			builder.password(passwordEncoder.encode(password));
		}else {
			builder=User.builder();
			builder.password(userDetails.getPassword());
		}
		UserDetails newUserDetails=builder.username(username)
											.authorities(userDetails.getAuthorities())
											.build();
		userDetailsManager.updateUser(newUserDetails);
		UserAccount account=accountRepository.findById(username).get();
		accountRepository.save(account);
	}
	
}	
