package com.mockcote.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mockcote.user.dto.User;
import com.mockcote.user.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	@Query("SELECT new com.mockcote.user.dto.User(u.id, u.userId, u.handle, u.level) FROM UserEntity u")
	List<User> findAllUser();

	@Query("SELECT new com.mockcote.user.dto.User(u.id, u.userId, u.handle, u.level) FROM UserEntity u where u.handle = :handle")
	User findUser(@Param("handle") String handle);
	
	@Query("SELECT u FROM UserEntity u WHERE u.userId = :userId")
	Optional<UserEntity> findByUserId(@Param("userId") String userId);

	@Modifying
	@Query("UPDATE UserEntity u SET u.pw = :newPassword WHERE u.handle = :handle")
	int updatePasswordByIdAndHandle(@Param("handle") String handle, @Param("newPassword")String newPassword);
	
	@Modifying
	@Query("DELETE FROM UserEntity u WHERE u.handle = :handle")
	int deleteUserByHandle(@Param("handle") String handle);
	
	@Modifying
	@Query("UPDATE UserEntity u SET u.refreshToken = :refreshToken WHERE u.userId = :userId")
	void updateRefreshToken(@Param("userId") String userId, @Param("refreshToken") String refreshToken);
	
	@Modifying
	@Query("UPDATE UserEntity u SET u.refreshToken = null WHERE u.userId = :userId")
	void deleteRefreshTokenByUserId(@Param("userId") String userId);
	
	@Modifying
	@Query("UPDATE UserEntity u SET u.level = :level WHERE u.userId = :userId")
	int updateLevel(@Param("userId") String userId, @Param("level") int level);
	
}
