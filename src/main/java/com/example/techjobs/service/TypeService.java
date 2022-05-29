package com.example.techjobs.service;

import com.example.techjobs.dto.outputDTO.OutputTypeDTO;
import java.util.List;

public interface TypeService {

  List<OutputTypeDTO> findAll();
}
