package com.example.techjobs.repository;

import com.example.techjobs.entity.Job;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {

  Optional<Job> findByIdAndStateNot(Integer jobId, String name);
}
