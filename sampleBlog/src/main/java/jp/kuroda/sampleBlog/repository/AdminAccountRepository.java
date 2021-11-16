package jp.kuroda.sampleBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.kuroda.sampleBlog.model.Account;

public interface AdminAccountRepository extends JpaRepository<Account, Integer>{
	public Account findByUsername(String username);
}
