package com.mockcote.user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mockcote.user.dto.JoinRequest;
import com.mockcote.user.dto.LoginRequest;
import com.mockcote.user.dto.User;
import com.mockcote.user.entity.UserEntity;
import com.mockcote.user.repository.UserRepository;
import com.mockcote.user.util.JwtUtil;
import com.mockcote.user.util.PasswordEncryptionUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepo;
	
	@Autowired
    private JwtUtil jwtUtil;
	
	@Transactional
	public int join(JoinRequest join) {
		try {
			String endcodedPw = PasswordEncryptionUtil.encryptPassword(join.getPw());
			UserEntity user = new UserEntity(join.getUserId(), endcodedPw, join.getHandle());
			
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
	
	@Transactional
	public Map<String, String> login(LoginRequest loginRequest) {
		
		String userId = loginRequest.getUserId();
		String rawPassword = loginRequest.getPassword();
		
        // DB에서 사용자 조회
        UserEntity userEntity = userRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId or password"));

        // 비밀번호 검증
        if (!PasswordEncryptionUtil.isPasswordMatch(rawPassword, userEntity.getPw())) {
            throw new IllegalArgumentException("Invalid userId or password");
        }
        
        // 로그인 성공 토큰 생성
        Map<String, String> tokens = new HashMap<>();
        String handle = userEntity.getHandle();
        String accessToken = jwtUtil.generateToken(userId,handle);
        String refreshToken = jwtUtil.generateRefreshToken(userId,handle);
        
        // refreshtoken DB에 저장
        userRepo.updateRefreshToken(userId, refreshToken);
        
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("handle", handle);
        return tokens;
    }
	
	
	@Transactional
    public int updateUserPassword(String handle, String rawPassword) {
        // 유효성 검사
        if (handle == null || handle.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Handle and password must not be null or empty.");
        }

        // 비밀번호 암호화
        String encryptedPassword = PasswordEncryptionUtil.encryptPassword(rawPassword);

        // 업데이트 작업
        int updatedRows = userRepo.updatePasswordByIdAndHandle(handle, encryptedPassword);

        // 결과 처리
        if (updatedRows > 0) {
            System.out.println("Password updated successfully for handle: " + handle);
            return updatedRows;
        } else {
            System.out.println("No matching user found for handle: " + handle);
            return 0;
        }
    }
	
	@Transactional
    public int deleteUserByHandle(String handle) {
        // 삭제 작업 수행
        int deletedRows = userRepo.deleteUserByHandle(handle);

        // 삭제된 레코드가 없으면 예외 발생
        if (deletedRows == 0) {
            throw new IllegalArgumentException("No user found with handle: " + handle);
        }

        // 성공 메시지 출력
        System.out.println("User with handle " + handle + " deleted successfully.");
        return deletedRows; // 삭제된 행의 수 반환
    }

	@Transactional
	public void deleteRefreshToken(String userId) {
		userRepo.deleteRefreshTokenByUserId(userId);
		
	}

}
