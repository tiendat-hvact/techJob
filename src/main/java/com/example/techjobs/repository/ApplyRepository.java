package com.example.techjobs.repository;

import com.example.techjobs.entity.Apply;
import com.example.techjobs.entity.ApplyId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyRepository extends JpaRepository<Apply, ApplyId> {}
