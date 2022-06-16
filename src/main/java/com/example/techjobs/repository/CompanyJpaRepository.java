package com.example.techjobs.repository;

import com.example.techjobs.entity.Company;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepository extends JpaRepository<Company, Integer> {

  Optional<Company> findByIdAndStateNot(Integer companyId, String stateNot);

  Optional<Company> findByEmailAndStateNot(String email, String stateNot);

  Optional<Company> findByIdNotAndEmailAndStateNot(
      Integer companyId, String email, String stateNot);

  Page<Company> findAllByStateNot(String stateNot, Pageable pageable);

  List<Company> findAllByStateNot(String stateNot);
}
