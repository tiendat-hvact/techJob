package com.example.techjobs.service;

import com.example.techjobs.dto.inputDTO.InputFileDTO;
import com.example.techjobs.dto.outputDTO.OutputFileDTO;

public interface FileService {

  OutputFileDTO findByUserId(Integer userId);

  void createOrUpdateFile(Integer userId, InputFileDTO data);
}
