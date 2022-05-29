package com.example.techjobs.service.impl;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.outputDTO.OutputTypeDTO;
import com.example.techjobs.repository.TypeRepository;
import com.example.techjobs.service.TypeService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TypeServiceImpl implements TypeService {

  private final TypeRepository typeRepository;
  private final GenericMapper genericMapper;

  public TypeServiceImpl(TypeRepository typeRepository, GenericMapper genericMapper) {
    this.typeRepository = typeRepository;
    this.genericMapper = genericMapper;
  }

  @Override
  public List<OutputTypeDTO> findAll() {
    return genericMapper.mapToListOfType(typeRepository.findAll(), OutputTypeDTO.class);
  }
}
