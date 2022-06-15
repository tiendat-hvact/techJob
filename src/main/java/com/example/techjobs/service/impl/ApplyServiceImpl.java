package com.example.techjobs.service.impl;

import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.entity.Apply;
import com.example.techjobs.entity.ApplyId;
import com.example.techjobs.entity.File;
import com.example.techjobs.entity.Job;
import com.example.techjobs.entity.User;
import com.example.techjobs.repository.ApplyRepository;
import com.example.techjobs.repository.FileRepository;
import com.example.techjobs.repository.JobRepository;
import com.example.techjobs.repository.UserRepository;
import com.example.techjobs.service.ApplyService;
import com.example.techjobs.service.EmailService;
import java.time.LocalDate;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplyServiceImpl implements ApplyService {

  private final ApplyRepository applyRepository;
  private final FileRepository fileRepository;
  private final UserRepository userRepository;
  private final JobRepository jobRepository;
  private final EmailService emailService;

  @Autowired
  public ApplyServiceImpl(
      ApplyRepository applyRepository,
      FileRepository fileRepository,
      UserRepository userRepository,
      JobRepository jobRepository,
      EmailService emailService) {
    this.applyRepository = applyRepository;
    this.fileRepository = fileRepository;
    this.userRepository = userRepository;
    this.jobRepository = jobRepository;
    this.emailService = emailService;
  }

  @Override
  public Integer applyJob(Integer jobId, Integer userId) {
    Job job = jobRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
    if (job == null) return 1;
    if (job.getDeadline().isBefore(LocalDate.now())) return 2;
    User user =
        userRepository.findByIdAndStateNot(userId, StateConstant.DELETED.name()).orElse(null);
    if (user == null) return 3;
    File file = fileRepository.findByUserId(userId).orElse(null);
    if (file == null) return 4;
    ApplyId applyId = new ApplyId(userId, jobId);
    Apply apply = applyRepository.findById(applyId).orElse(null);
    if (apply != null) return 5;
    apply = new Apply(applyId, user, job, LocalDate.now());
    applyRepository.save(apply);
    emailService.sendEmailNotifyJobApplied(
        job.getCompany().getEmail(), job.getName(), file.getUrl());
    return 0;
  }
}
