package jp.kuroda.sampleBlog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.model.UserAccount;
import jp.kuroda.sampleBlog.repository.PersonRepository;
import jp.kuroda.sampleBlog.repository.UserAccountRepository;

@SpringJUnitConfig
public class UserServiceTest {
	@TestConfiguration
	static class Config{
		@Bean
		public UserDetailsManager userDetailsManager() {
			return new InMemoryUserDetailsManager();
		}
		@Bean
		public UserService userService() {
			return new UserService();
		}
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
		@Bean
		public PersonService personService() {
			return new PersonService();
		}
	}
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDetailsManager userDetailsManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private PersonService personService;
	
	@MockBean
	private UserAccountRepository accountRepository;
	@MockBean
	private PersonRepository personRepository;
	
	@Test
	public void testFind() {
		UserAccount account=new UserAccount();
		when(accountRepository.findById("chikara")).thenReturn(Optional.of(account));
		
		UserAccount a=userService.find("chikara");
		verify(accountRepository).findById("chikara");
	}
	@Test
	public void testCreateUserAccount() {
		UserAccount account=userService.createUserAccount("chikara10", "chikara1049", "USER");
		UserDetails userDetails=userDetailsManager.loadUserByUsername("chikara10");
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.isEnabled()).isEqualTo(true);
		for(GrantedAuthority auth:userDetails.getAuthorities()) {
			assertThat(auth.getAuthority()).isEqualTo("ROLE_USER");
		}
		verify(accountRepository).save(account);
		assertThat(account.getPerson()).isNotNull();
	}
	@Test
	public void testUpdateUserAccountChangePassword() {
		UserBuilder builder=User.builder();
		UserDetails userDetails=builder.username("chikarachikara")
										.password(passwordEncoder.encode("chikara"))
										.roles("USER")
										.build();
		userDetailsManager.createUser(userDetails);
		UserAccount account=new UserAccount();
		when(accountRepository.findById("chikarachikara")).thenReturn(Optional.of(account));
		userService.updateUserAccount("chikarachikara", "chikarachikara");
		UserDetails newUserDetails=userDetailsManager.loadUserByUsername("chikarachikara");
		assertThat(newUserDetails.getPassword()).isNotEqualTo(userDetails.getPassword());
	}
	
	@Test
	public void testUpdateUserAccountNotChangePassword() {
		UserBuilder builder=User.builder();
		UserDetails userDetails=builder.username("chikarachikara1")
										.password(passwordEncoder.encode("chikara"))
										.roles("USER")
										.build();
		userDetailsManager.createUser(userDetails);
		UserAccount account=new UserAccount();
		when(accountRepository.findById("chikarachikara1")).thenReturn(Optional.of(account));
		userService.updateUserAccount("chikarachikara1", "");
		UserDetails newUserDetails=userDetailsManager.loadUserByUsername("chikarachikara");
		assertThat(newUserDetails.getPassword()).isNotEqualTo(userDetails.getPassword());
	}
	
}
