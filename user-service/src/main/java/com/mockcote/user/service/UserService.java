package com.mockcote.user.service;

import java.util.List;
import java.util.Map;

import com.mockcote.user.dto.JoinRequest;
import com.mockcote.user.dto.LoginRequest;
import com.mockcote.user.dto.User;

public interface UserService {
	public int join(JoinRequest join);
	
	public List<User> selectAll();
	public User selectUser(String handle);
	
	public Map<String, String> login(LoginRequest loginRequest);
	
	public int updateUserPassword(String handle, String rawPassword);
	public int deleteUserByHandle(String handle);
	public void deleteRefreshToken(String refreshToken);
}
