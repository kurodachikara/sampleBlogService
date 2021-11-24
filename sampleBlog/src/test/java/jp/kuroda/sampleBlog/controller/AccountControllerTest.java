package jp.kuroda.sampleBlog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jp.kuroda.sampleBlog.model.Account;
import jp.kuroda.sampleBlog.model.Person;
import jp.kuroda.sampleBlog.model.UserAccount;
import jp.kuroda.sampleBlog.repository.AdminAccountRepository;
import jp.kuroda.sampleBlog.repository.PersonRepository;
import jp.kuroda.sampleBlog.repository.UserAccountRepository;
import jp.kuroda.sampleBlog.service.PersonService;
import jp.kuroda.sampleBlog.service.UserService;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;
	@MockBean
	private PersonService personService;
	@MockBean
	private UserDetailsManager manager;
	@MockBean
	private AdminAccountRepository adminRepository;
	@MockBean
	private UserAccountRepository userAccountRepository;
	@MockBean
	private PersonRepository personRepository;
	
	private static UserAccount userAccount;
	private static Account account;
	private static List<UserAccount> userAccounts;
	
	@BeforeEach
	public void setUp() {
		Person person=new Person();
		
		account=new Account();
		account.setUsername("kurodachikara");
		account.setPassword("chikara0604");
		when(userService.findAdmin("user")).thenReturn(account);
		
		userAccount=new UserAccount();
		userAccount.setType("admin");
		userAccount.setAccount(account);
		userAccount.setPerson(person);
		userAccount.setUsername("kurodachikara");
		userAccount.setPassword("chikara0604");		
		userAccounts=Arrays.asList(userAccount);
		when(userService.find("user")).thenReturn(userAccount);
	}
	
	@TestConfiguration
	static class Config implements WebMvcConfigurer{

		@Override
		public void addFormatters(FormatterRegistry registry) {
			registry.addConverter(String.class,UserAccount.class,id->userAccount);
			registry.addConverter(String.class,Account.class,id->account);
			registry.addConverter(String.class,Person.class,id->userAccount.getPerson());
		}
	}
	@Test
	public void testIndexNotLigin() throws Exception{
		mockMvc.perform(get("/account"))
				.andExpect(status().isFound());
	}
	@Test
	@WithMockUser
	public void testIndexHasNotPermission() throws Exception{
		mockMvc.perform(get("/account"))
				.andExpect(status().isForbidden());
	}
	@Test
	@WithMockUser(roles="USER")
	public void testIndexHasAthorPermission() throws Exception{
		mockMvc.perform(get("/account"))
		.andExpect(status().isForbidden());
	}
	@Test
	@WithMockUser(roles="ADMIN")
	public void testIndexHasPermission() throws Exception{
		when(userService.getAccountList()).thenReturn(userAccounts);
		when(userService.createAdminAccount(userAccount.getUsername(), userAccount.getPassword(), "ADMIN")).thenReturn(account);
		mockMvc.perform(get("/account"))
				.andExpect(status().isOk())
				.andExpect(view().name("account/index"))
				.andExpect(model().attribute("userAccounts", userAccounts));	
	}
	@Test
	@WithMockUser(roles="ADMIN")
	public void testDeleteAccount() throws Exception{
		mockMvc.perform(get("/account/delete/kurodachikara"))
				.andExpect(status().isFound());
		verify(userService).deleteUser(userAccount);
	}
	@Test
	@WithMockUser(roles="ADMIN")
	public void testEditAdminAccountGet() throws Exception{
		MvcResult result=mockMvc.perform(get("/account/1/edit"))
								.andExpect(status().isOk())
								.andExpect(view().name("account/edit"))
								.andReturn();
		Account account1=(Account)result.getModelAndView().getModel().get("account");
		assertThat(account1.getUsername()).isEqualTo(account.getUsername());
		assertThat(account1.getPassword()).isEqualTo(account.getPassword());
	}
	@Test
	@WithMockUser(roles="ADMIN")
	public void testEditAdminAccountPostSuccess() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("username", "chikara");
		params.add("password", "chikarachikara");
		mockMvc.perform(post("/account/1/edit").with(csrf()).params(params))
				.andExpect(status().isFound());
	}
	@Test
	@WithMockUser(roles="ADMIN")
	public void testCreateAdminGet() throws Exception{
		mockMvc.perform(get("/account/createAdmin"))
				.andExpect(status().isOk())
				.andExpect(view().name("account/create"));
	}
	@Test
	@WithMockUser(roles="ADMIN")
	public void testCreateAdminPostSuccess() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("username", "minato");
		params.add("password", "minato1013");
		mockMvc.perform(post("/account/createAdmin").with(csrf()).params(params))
				.andExpect(status().isFound());
		verify(userService).createAdminAccount("minato", "minato1013","ADMIN");
	}
}
