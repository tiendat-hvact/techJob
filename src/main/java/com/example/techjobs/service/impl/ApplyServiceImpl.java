package com.example.techjobs.service.impl;

import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.entity.Apply;
import com.example.techjobs.entity.ApplyId;
import com.example.techjobs.entity.File;
import com.example.techjobs.entity.Job;
import com.example.techjobs.entity.User;
import com.example.techjobs.repository.ApplyJpaRepository;
import com.example.techjobs.repository.FileJpaRepository;
import com.example.techjobs.repository.JobJpaRepository;
import com.example.techjobs.repository.UserJpaRepository;
import com.example.techjobs.service.ApplyService;
import com.example.techjobs.service.EmailService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplyServiceImpl implements ApplyService {

  private final ApplyJpaRepository applyJpaRepository;
  private final FileJpaRepository fileJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final JobJpaRepository jobJpaRepository;
  private final EmailService emailService;

  @Autowired
  public ApplyServiceImpl(
      ApplyJpaRepository applyJpaRepository,
      FileJpaRepository fileJpaRepository,
      UserJpaRepository userJpaRepository,
      JobJpaRepository jobJpaRepository,
      EmailService emailService) {
    this.applyJpaRepository = applyJpaRepository;
    this.fileJpaRepository = fileJpaRepository;
    this.userJpaRepository = userJpaRepository;
    this.jobJpaRepository = jobJpaRepository;
    this.emailService = emailService;
  }

  @Override
  public Integer applyJob(Integer jobId, Integer userId) {
    Job job =
        jobJpaRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
    if (job == null) return 1;
    if (job.getDeadline().isBefore(LocalDate.now())) return 2;
    User user =
        userJpaRepository.findByIdAndStateNot(userId, StateConstant.DELETED.name()).orElse(null);
    if (user == null) return 3;
    File file = fileJpaRepository.findByUserId(userId).orElse(null);
    if (file == null) return 4;
    ApplyId applyId = new ApplyId(userId, jobId);
    Apply apply = applyJpaRepository.findById(applyId).orElse(null);
    if (apply != null) return 5;
    apply = new Apply(applyId, user, job, LocalDate.now());
    applyJpaRepository.save(apply);
    emailService.sendEmailNotifyJobApplied(
        job.getCompany().getEmail(), job.getName(), file.getUrl());
    return 0;
  }
}
