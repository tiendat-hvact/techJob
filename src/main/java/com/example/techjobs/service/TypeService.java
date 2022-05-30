package com.example.techjobs.service;

import com.example.techjobs.dto.outputDTO.OutputTypeDTO;
import java.util.List;

public interface TypeService {

  /** Lấy ra danh sách các ngành nghề công việc */
  List<OutputTypeDTO> findAll();
}
