package com.example.techjobs.service.impl;

import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.entity.Apply;
import com.example.techjobs.entity.ApplyId;
import com.example.techjobs.entity.File;
import com.example.techjobs.entity.Job;
import com.example.techjobs.entity.User;
import com.example.techjobs.repository.ApplyRepository;
import com.example.techjobs.repository.CompanyRepository;
import com.example.techjobs.repository.FileRepository;
import com.example.techjobs.repository.JobRepository;
import com.example.techjobs.repository.UserRepository;
import com.example.techjobs.service.ApplyService;
import com.example.techjobs.service.EmailService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplyServiceImpl implements ApplyService {

  private final CompanyRepository companyRepository;
  private final ApplyRepository applyRepository;
  private final FileRepository fileRepository;
  private final UserRepository userRepository;
  private final JobRepository jobRepository;
  private final EmailService emailService;

  @Autowired
  public ApplyServiceImpl(
      CompanyRepository companyRepository,
      ApplyRepository applyRepository,
      FileRepository fileRepository,
      UserRepository userRepository,
      JobRepository jobRepository,
      EmailService emailService) {
    this.companyRepository = companyRepository;
    this.applyRepository = applyRepository;
    this.fileRepository = fileRepository;
    this.userRepository = userRepository;
    this.jobRepository = jobRepository;
    this.emailService = emailService;
  }

  @Override
  public boolean applyJob(Integer jobId, Integer userId) {
    Job job = jobRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
    User user =
        userRepository.findByIdAndStateNot(userId, StateConstant.DELETED.name()).orElse(null);
    File file = fileRepository.findByUserId(userId).orElse(null);
    if (job != null && user != null && file != null) {
      ApplyId applyId = new ApplyId(userId, jobId);
      Apply apply = new Apply(applyId, user, job, LocalDate.now());
      applyRepository.save(apply);
      emailService.sendEmailNotifyJobApplied(
          job.getCompany().getEmail(), job.getName(), file.getUrl());
    }
    return false;
  }
}
