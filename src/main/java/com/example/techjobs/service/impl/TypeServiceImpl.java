package com.example.techjobs.service.impl;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.outputDTO.OutputTypeDTO;
import com.example.techjobs.entity.Job;
import com.example.techjobs.entity.Type;
import com.example.techjobs.repository.JobRepository;
import com.example.techjobs.repository.TypeRepository;
import com.example.techjobs.service.TypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TypeServiceImpl implements TypeService {

    private final TypeRepository typeRepository;
    private final GenericMapper genericMapper;
    private final JobRepository jobRepository;

    @Override
    public List<OutputTypeDTO> findAll() {
        return genericMapper.mapToListOfType(typeRepository.findAll(), OutputTypeDTO.class);
    }

    @Override
    public ResultDTO<Type> save(Type type) {
        Optional<Type> typeByName = this.typeRepository.findByName(type.getName());
        if (typeByName.isPresent()) {
            return new ResultDTO<>(null, true, "Tên ngành nghề đã tồn tại");
        }
        Type save = this.typeRepository.save(type);
        return new ResultDTO<>(save, false, "Thêm mới ngành nghề thành công");
    }

    @Override
    public ResultDTO delete(Integer id) {

        if (!this.typeRepository.findById(id).isPresent()) {
            return new ResultDTO(null, true, "Ngành nghề không tồn tại");
        }

        List<Job> jobs = this.jobRepository.findByTypeId(id);
        if (!jobs.isEmpty()) {
            return new ResultDTO(null, true, "Ngành nghề đang được sử dụng");
        }
        this.typeRepository.deleteById(id);
        return new ResultDTO(null, false, "Xóa ngành nghề thành công");
    }
}
