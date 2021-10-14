package jp.kuroda.sampleBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.kuroda.sampleBlog.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, String>{

}
