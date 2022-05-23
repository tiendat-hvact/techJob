package com.example.techjobs.repository;

import com.example.techjobs.entity.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

  Optional<Company> findByIdAndStateNot(Integer companyId, String stateNot);

  Optional<Company> findByEmailAndStateNot(String email, String stateNot);
}
