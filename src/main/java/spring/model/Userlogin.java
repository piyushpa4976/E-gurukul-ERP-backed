package spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "userlogin")
public class Userlogin {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userid;
	private String name;
	@Column(unique = true)
	private String email;
	private String password;
	private String userrole;
	private boolean is_currently_logged_in;
	private String last_logged_in;
	
	private String token;
	private String token_generation_time;
	private String token_check_time;
	
	
	
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getToken_generation_time() {
		return token_generation_time;
	}
	public void setToken_generation_time(String token_generation_time) {
		this.token_generation_time = token_generation_time;
	}
	public String getToken_check_time() {
		return token_check_time;
	}
	public void setToken_check_time(String token_check_time) {
		this.token_check_time = token_check_time;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserrole() {
		return userrole;
	}
	public void setUserrole(String userrole) {
		this.userrole = userrole;
	}
	
	public boolean isIs_currently_logged_in() {
		return is_currently_logged_in;
	}
	public void setIs_currently_logged_in(boolean is_currently_logged_in) {
		this.is_currently_logged_in = is_currently_logged_in;
	}
	public String getLast_logged_in() {
		return last_logged_in;
	}
	public void setLast_logged_in(String last_logged_in) {
		this.last_logged_in = last_logged_in;
	}


	
}
