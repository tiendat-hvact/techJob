package com.example.techjobs.repository;

import com.example.techjobs.entity.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByIdAndStateNot(Integer userId, String stateNot);

  Optional<User> findByEmailAndStateNot(String email, String stateNot);

  Optional<User> findByRole(String role);

  Optional<User> findByIdNotAndEmailAndStateNot(Integer userId, String email, String stateNot);

  @Query(nativeQuery = true, value = "SELECT * FROM users WHERE state <> 'DELETED' AND role <> 'ADMIN'")
  List<User> findActiveUser();
}
