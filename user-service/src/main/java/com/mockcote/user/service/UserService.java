package com.mockcote.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mockcote.user.dto.JoinRequest;
import com.mockcote.user.dto.User;
import com.mockcote.user.entity.UserEntity;
import com.mockcote.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepo;
	
	@Transactional
	public int join(JoinRequest join) {
		try {
			UserEntity user = new UserEntity(join.getUserId(), join.getPw(), join.getHandle());
			
			UserEntity result = userRepo.save(user);
			
			System.out.println("Join Entity: " + result);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public List<User> selectAll() {
		List<User> list = userRepo.findAllUser();
		
		return list;
	}
	
	public User selectUser(String handle) {
		User user = userRepo.findUser(handle);
		
		return user;
	}
	
}
