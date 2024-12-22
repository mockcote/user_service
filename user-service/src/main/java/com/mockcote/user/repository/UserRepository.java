package com.mockcote.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mockcote.user.dto.User;
import com.mockcote.user.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>{
	
	@Query("SELECT new com.mockcote.user.dto.User(u.id, u.userId, u.handle) FROM UserEntity u")
    List<User> findAllUser();
	
	@Query("SELECT new com.mockcote.user.dto.User(u.id, u.userId, u.handle) FROM UserEntity u where u.handle = :handle")
    User findUser(@Param("handle") String handle);
}
