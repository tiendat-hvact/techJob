package com.example.techjobs.service.impl;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.inputDTO.InputFileDTO;
import com.example.techjobs.dto.outputDTO.OutputFileDTO;
import com.example.techjobs.entity.File;
import com.example.techjobs.repository.FileRepository;
import com.example.techjobs.service.FileService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

  private final GenericMapper genericMapper;
  private final FileRepository fileRepository;

  @Autowired
  public FileServiceImpl(GenericMapper genericMapper, FileRepository fileRepository) {
    this.genericMapper = genericMapper;
    this.fileRepository = fileRepository;
  }

  @Override
  public OutputFileDTO findByUserId(Integer userId) {
    File file = fileRepository.findByUserId(userId).orElse(null);
    return genericMapper.mapToType(file, OutputFileDTO.class);
  }

  @Override
  public void createOrUpdateFile(Integer userId, InputFileDTO data) {
    File file = fileRepository.findByUserId(userId).orElse(null);
    if (file != null) {
      genericMapper.copyNonNullProperties(data, file);
    } else {
      file = genericMapper.mapToType(data, File.class);
    }
    file.setCreateDate(LocalDate.now());
    fileRepository.save(file);
  }
}
