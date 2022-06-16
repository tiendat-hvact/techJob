package com.example.techjobs.repository;

import com.example.techjobs.entity.File;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<File, Integer> {

  Optional<File> findByUserId(Integer userId);
}
