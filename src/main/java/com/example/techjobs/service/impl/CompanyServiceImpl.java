package com.example.techjobs.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.techjobs.common.encryptor.AttributeEncryptor;
import com.example.techjobs.common.enums.CityConstant;
import com.example.techjobs.common.enums.ImgConstant;
import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.common.util.Utils;
import com.example.techjobs.dto.LoginRequest;
import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.inputDTO.InputCompanyDTO;
import com.example.techjobs.dto.outputDTO.OutputCompanyDTO;
import com.example.techjobs.entity.Company;
import com.example.techjobs.entity.Following;
import com.example.techjobs.entity.User;
import com.example.techjobs.repository.CompanyJpaRepository;
import com.example.techjobs.repository.FollowingJpaRepository;
import com.example.techjobs.repository.JobJpaRepository;
import com.example.techjobs.repository.UserJpaRepository;
import com.example.techjobs.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

  private final FollowingJpaRepository followingJpaRepository;
  private final CompanyJpaRepository companyJpaRepository;
  private final AttributeEncryptor attributeEncryptor;
  private final UserJpaRepository userJpaRepository;
  private final JobJpaRepository jobJpaRepository;
  private final EmailServiceImpl emailService;
  private final GenericMapper genericMapper;
  private final Cloudinary cloudinary;

  @Override
  public OutputCompanyDTO findById(Integer companyId) {
    Company company =
        companyJpaRepository
            .findByIdAndStateNot(companyId, StateConstant.DELETED.name())
            .orElse(null);
    OutputCompanyDTO outputCompanyDTO = genericMapper.mapToType(company, OutputCompanyDTO.class);
    if (outputCompanyDTO != null) {
      outputCompanyDTO.setCity(
          CityConstant.getEnumKeyForValue(Objects.requireNonNull(company).getCity()));
      outputCompanyDTO.setNumberJob(
          jobJpaRepository.countNumberJob(companyId, StateConstant.DELETED.name()));
    }
    return outputCompanyDTO;
  }

  @Override
  public List<OutputCompanyDTO> findLimit(Integer limit) {
    Page<Company> companies =
        companyJpaRepository.findAllByStateNot(
            StateConstant.DELETED.name(),
            PageRequest.of(0, limit, Sort.by(Direction.DESC, "createDate")));
    return genericMapper.mapToListOfType(companies.getContent(), OutputCompanyDTO.class);
  }

  @Override
  public Map<String, Object> loginAccount(LoginRequest data) {
    Map<String, Object> result = null;
    Company company =
        companyJpaRepository
            .findByEmailAndStateNot(data.getEmail(), StateConstant.DELETED.name())
            .orElse(null);
    if (company != null) {
      if (attributeEncryptor.matches(data.getPassword(), company.getPassword())) {
        result = new HashMap<>();
        result.put("accountId", company.getId());
        if (company.getState().equals(StateConstant.WAIT.name())) {
          result.put("verify", false);
        } else {
          result.put("verify", true);
        }
      }
    }
    return result;
  }

  @Override
  public List<Company> findAll() {
    return this.companyJpaRepository.findAllByStateNot(StateConstant.DELETED.name());
  }

  @Override
  public Boolean checkVerifyCode(Integer accountId, String verifyCode) {
    Company company =
        companyJpaRepository
            .findByIdAndStateNot(accountId, StateConstant.DELETED.name())
            .orElse(null);
    if (company != null && attributeEncryptor.matches(verifyCode, company.getVerifyCode())) {
      company.setState(StateConstant.ACTIVE.name());
      company.setUpdateDate(LocalDate.now());
      companyJpaRepository.save(company);
      return true;
    }
    return false;
  }

  @Override
  @Transactional
  @SneakyThrows
  public boolean createCompany(InputCompanyDTO data) {
    Company company =
        companyJpaRepository
            .findByEmailAndStateNot(data.getEmail(), StateConstant.DELETED.name())
            .orElse(null);
    if (company == null) {
      String verifyCode = Utils.createVerifyCode();
      company = genericMapper.mapToType(data, Company.class);
      company.setAvatar(ImgConstant.UnknownCompany.getValue());
      company.setPassword(attributeEncryptor.convertToDatabaseColumn(data.getPassword()));
      company.setVerifyCode(attributeEncryptor.convertToDatabaseColumn(verifyCode));
      company.setState(StateConstant.WAIT.name());
      company.setCreateBy("unknow");
      company.setCreateDate(LocalDate.now());
      company.setUpdateBy("unknow");
      company.setUpdateDate(LocalDate.now());
      emailService.sendEmailVerifyCode(data.getEmail(), verifyCode);
      companyJpaRepository.save(company);
      return true;
    }
    return false;
  }

  @Override
  @Transactional
  @SneakyThrows
  public boolean updateCompany(int companyId, InputCompanyDTO data) {
    Company companyDuplicatedEmail =
        companyJpaRepository
            .findByIdNotAndEmailAndStateNot(
                companyId, data.getEmail(), StateConstant.DELETED.name())
            .orElse(null);
    Company company =
        companyJpaRepository
            .findByIdAndStateNot(companyId, StateConstant.DELETED.name())
            .orElse(null);
    if (companyDuplicatedEmail == null && company != null) {
      genericMapper.copyNonNullProperties(data, company);
      String name = Utils.formatFileName(company.getEmail()) + "_" + System.currentTimeMillis();
      if (data.getFile() != null && !data.getFile().isEmpty()) {
        Transformation incoming =
            new Transformation<>()
                .gravity("face")
                .height(400)
                .width(400)
                .crop("crop")
                .chain()
                .radius("max")
                .chain()
                .width(100)
                .crop("scale");
        this.cloudinary
            .uploader()
            .upload(
                data.getFile().getBytes(),
                ObjectUtils.asMap(
                    "resource_type",
                    "auto",
                    "public_id",
                    "company/" + name,
                    "transformation",
                    incoming));
        company.setAvatar(ImgConstant.Prefix.getValue() + "company/" + name);
      }
      company.setCity(CityConstant.valueOf(company.getCity()).getValue());
      company.setUpdateDate(LocalDate.now());
      companyJpaRepository.save(company);
      return true;
    }
    return false;
  }

  @Override
  public ResultDTO<Company> delete(Integer id) {
    Optional<Company> companyOptional = this.companyJpaRepository.findById(id);
    if (companyOptional.isEmpty()) {
      return new ResultDTO<>(null, true, "Không tìm thấy cong ty");
    }
    Company company = companyOptional.get();
    company.setState(StateConstant.DELETED.name());
    this.companyJpaRepository.save(company);
    return new ResultDTO<>(null, false, "Xóa company thành công");
  }

  @Override
  public void followCompany(Integer idCompany, Integer idUser) {
    Company company = this.companyJpaRepository.findById(idCompany).orElse(null);
    if (Objects.isNull(company)) {
      return;
    }

    User user = this.userJpaRepository.findById(idUser).orElse(null);
    if (Objects.isNull(user)) {
      return;
    }


    Following following = this.followingJpaRepository.findByCompanyIdAndUserid(idCompany, idUser).orElse(null);
    if (Objects.nonNull(following)) {
      return;
    }

    following = new Following();
    following.setCompany(company);
    following.setUser(user);
    following.setCreateDate(LocalDate.now());
    this.followingJpaRepository.save(following);
  }

  @Override
  public void unfollowCompany(Integer idCompany, Integer idUser) {
    Company company = this.companyJpaRepository.findById(idCompany).orElse(null);
    if (Objects.isNull(company)) {
      return;
    }

    User user = this.userJpaRepository.findById(idUser).orElse(null);
    if (Objects.isNull(user)) {
      return;
    }


    Following following = this.followingJpaRepository.findByCompanyIdAndUserid(idCompany, idUser).orElse(null);
    if (Objects.isNull(following)) {
      return;
    }
    this.followingJpaRepository.deleteById(following.getId());
  }

  @Override
  public boolean checkFollowing(Integer idCompany, Integer userId) {
    Optional<Following> following = this.followingJpaRepository.findByCompanyIdAndUserid(idCompany, userId);
    return following.isPresent();
  }

  @Override
  public List<Company> getCompanyFollowing(Integer userId) {
    if (!this.userJpaRepository.findById(userId).isPresent()) return new ArrayList<>();
    return this.companyJpaRepository.getCompanyFollowing(userId);
  }
}
