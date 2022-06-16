package com.example.techjobs.service;

import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.outputDTO.OutputTypeDTO;
import com.example.techjobs.entity.Type;
import java.util.List;

public interface TypeService {

  /** Lấy ra danh sách các ngành nghề công việc */
  List<OutputTypeDTO> findAll();

  ResultDTO<Type> save(Type type);

  ResultDTO<Type> delete(Integer id);
}
