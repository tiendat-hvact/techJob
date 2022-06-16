package com.example.techjobs.repository;

import com.example.techjobs.entity.Job;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Integer> {

  Optional<Job> findByIdAndStateNot(Integer jobId, String stateNot);

  Optional<Job> findByIdNotAndNameAndStateNot(Integer jobId, String name, String stateNot);

  @Query(
          nativeQuery = true,
          value = "SELECT * FROM jobs" +
                  " WHERE (:name IS NULL OR name LIKE :name)" +
                  " AND (:typeId IS NULL OR type_id = :typeId)" +
                  " AND state <> :state" +
                  " ORDER BY create_date DESC")
  Page<Job> searchJob(@Param("name") String ex, @Param("typeId") Integer typeId, @Param("state") String stateNot, Pageable pageable);

  Page<Job> findAllByCompanyIdAndStateNot(Integer companyId, String stateNot, Pageable pageable);

  @Query(nativeQuery = true, value = "SELECT * FROM jobs WHERE type_id = ?1")
  List<Job> findByTypeId(Integer typeId);

  @Query(nativeQuery = true, value = "SELECT * FROM jobs WHERE state <> 'DELETED'")
  List<Job> findActiveJob();
}
