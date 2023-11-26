package View;

public class User {
	private int userID;
	private String username;
	private String password;

	public User(int userID, String username, String password) {
		this.userID = userID;
		this.username = username;
		this.password = password;
	}

	public int getUserID() {
		return this.userID;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}
	}