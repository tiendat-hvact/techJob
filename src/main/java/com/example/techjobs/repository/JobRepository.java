package com.example.techjobs.repository;

import com.example.techjobs.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {}
