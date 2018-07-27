package edu.neu.service;

import edu.neu.model.User;

public interface UserService {
	public User findUserByEmail(String email);
	public void saveUser(User user);
	public void updateUser(User user);
}
