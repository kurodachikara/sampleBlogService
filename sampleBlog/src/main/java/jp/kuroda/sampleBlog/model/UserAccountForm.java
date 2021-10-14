package jp.kuroda.sampleBlog.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UserAccountForm {
	private String type;
	
	@NotBlank(message="ユーザーIDを入力してください")
	private String username;
	
	@NotBlank(message="パスワードを入力してください")
	@Size(min=5,message="パスワードは５文字以上で設定してください")
	private String password;
	
}
