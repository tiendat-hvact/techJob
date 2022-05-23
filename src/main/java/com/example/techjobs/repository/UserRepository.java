package com.example.techjobs.repository;

import com.example.techjobs.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByIdAndStateNot(Integer userId, String stateNot);

  Optional<User> findByEmailAndStateNot(String email, String stateNot);

  Optional<User> findByIdNotAndEmailAndStateNot(Integer userId, String email, String stateNot);
}
