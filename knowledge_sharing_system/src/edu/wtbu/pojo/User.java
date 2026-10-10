package edu.wtbu.pojo;

public class User {
	//与数据库字段对应
    private Long id;      
    private String username;   
    private String password;   
    private String email;     
    private String phone;      
    private Integer role;      // 0=普通用户，1=管理员
    
    // 无参构造方法
    public User() {}
    
    // 有参构造方法
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    //  getter和setter方法
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}


	  /**
     * 判断是否是管理员
     */
    public boolean isAdmin() {
        return role != null && role == 1;
    }
    
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", role=" + role + "]";
    }
}
