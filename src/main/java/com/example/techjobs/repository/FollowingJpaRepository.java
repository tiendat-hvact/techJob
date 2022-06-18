package com.example.techjobs.repository;

import com.example.techjobs.entity.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FollowingJpaRepository extends JpaRepository<Following, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM followings WHERE company_id = ?1 AND user_id = ?2")
    Optional<Following> findByCompanyIdAndUserid(Integer companyId, Integer userId);

    @Query(nativeQuery = true, value = "SELECT * FROM followings WHERE job_id = ?1 AND user_id = ?2")
    Optional<Following> findByJobIdAndUserid(Integer jobId, Integer userId);
}
