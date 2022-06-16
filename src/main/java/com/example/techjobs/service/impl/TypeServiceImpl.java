package com.example.techjobs.service.impl;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.outputDTO.OutputTypeDTO;
import com.example.techjobs.entity.Job;
import com.example.techjobs.entity.Type;
import com.example.techjobs.repository.JobJpaRepository;
import com.example.techjobs.repository.TypeJpaRepository;
import com.example.techjobs.service.TypeService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TypeServiceImpl implements TypeService {

  private final TypeJpaRepository typeJpaRepository;
  private final JobJpaRepository jobJpaRepository;
  private final GenericMapper genericMapper;

  @Override
  public List<OutputTypeDTO> findAll() {
    return genericMapper.mapToListOfType(typeJpaRepository.findAll(), OutputTypeDTO.class);
  }

  @Override
  public ResultDTO<Type> save(Type type) {
    Optional<Type> typeByName = this.typeJpaRepository.findByName(type.getName());
    if (typeByName.isPresent()) {
      return new ResultDTO<>(null, true, "Tên ngành nghề đã tồn tại");
    }
    Type save = this.typeJpaRepository.save(type);
    return new ResultDTO<>(save, false, "Thêm mới ngành nghề thành công");
  }

  @Override
  public ResultDTO<Type> delete(Integer id) {

    if (this.typeJpaRepository.findById(id).isEmpty()) {
      return new ResultDTO<>(null, true, "Ngành nghề không tồn tại");
    }
    List<Job> jobs = this.jobJpaRepository.findAllByTypeId(id);
    if (!jobs.isEmpty()) {
      return new ResultDTO<>(null, true, "Ngành nghề đang được sử dụng");
    }
    this.typeJpaRepository.deleteById(id);
    return new ResultDTO<>(null, false, "Xóa ngành nghề thành công");
  }
}
