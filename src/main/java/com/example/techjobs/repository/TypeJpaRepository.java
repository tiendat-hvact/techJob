package com.example.techjobs.repository;

import com.example.techjobs.entity.Type;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeJpaRepository extends JpaRepository<Type, Integer> {

  Optional<Type> findByName(String name);
}
