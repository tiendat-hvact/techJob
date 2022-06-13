package com.example.techjobs.service.impl;

import com.example.techjobs.common.enums.GenderConstant;
import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import com.example.techjobs.entity.Company;
import com.example.techjobs.entity.Job;
import com.example.techjobs.repository.CompanyRepository;
import com.example.techjobs.repository.JobRepository;
import com.example.techjobs.service.JobService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl implements JobService {

  private final CompanyRepository companyRepository;
  private final JobRepository jobRepository;
  private final GenericMapper genericMapper;

  public JobServiceImpl(
      CompanyRepository companyRepository,
      JobRepository jobRepository,
      GenericMapper genericMapper) {
    this.companyRepository = companyRepository;
    this.jobRepository = jobRepository;
    this.genericMapper = genericMapper;
  }

  @Override
  public OutputJobDTO findById(Integer jobId) {
    Job job = jobRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
    if (job != null) {
      job.setGender(GenderConstant.valueOf(job.getGender()).getValue());
    }
    return genericMapper.mapToType(job, OutputJobDTO.class);
  }

  @Override
  public List<OutputJobDTO> findLimit(Integer limit) {
    Page<Job> jobs =
        jobRepository.findAllAndStateNot(
            StateConstant.DELETED.name(),
            PageRequest.of(0, limit, Sort.by(Direction.DESC, "createDate")));
    return genericMapper.mapToListOfType(jobs.getContent(), OutputJobDTO.class);
  }

  @Override
  public Integer createJob(Integer companyId, InputJobDTO data) {
    Company company =
        companyRepository.findByIdAndStateNot(companyId, StateConstant.DELETED.name()).orElse(null);
    if (company != null) {
      Job job = genericMapper.mapToType(data, Job.class);
      job.setCompany(company);
      job.setState(StateConstant.ACTIVE.name());
      job.setCreateBy(company.getName());
      job.setCreateDate(LocalDate.now());
      job.setUpdateBy(company.getName());
      job.setUpdateDate(LocalDate.now());
      jobRepository.save(job);
      return job.getId();
    }
    return 0;
  }
}
