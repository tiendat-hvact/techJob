package com.example.techjobs.controller;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.common.util.Utils;
import com.example.techjobs.dto.NotificationRequest;
import com.example.techjobs.dto.inputDTO.InputCompanyDTO;
import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.outputDTO.OutputCompanyDTO;
import com.example.techjobs.service.CompanyService;
import com.example.techjobs.service.JobService;
import com.example.techjobs.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/techJob/company")
public class CompanyController {

  private final CompanyService companyService;
  private final GenericMapper genericMapper;
  private final TypeService typeService;
  private final JobService jobService;

  @Autowired
  public CompanyController(
      CompanyService companyService,
      GenericMapper genericMapper,
      TypeService typeService,
      JobService jobService) {
    this.companyService = companyService;
    this.genericMapper = genericMapper;
    this.typeService = typeService;
    this.jobService = jobService;
  }

  @GetMapping
  public String companyFindById(
      Model model,
      @ModelAttribute(name = "notification") NotificationRequest notification,
      @CookieValue(name = "company", defaultValue = "0") int companyId) {
    if (companyId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    OutputCompanyDTO company = companyService.findById(companyId);
    if (company != null) {
      if (notification.getText() != null) {
        switch (notification.getText()) {
          case "update-fail":
            notification.setText(
                "Cập nhật thông tin thất bại <br> Email mới của công ty đã có trong hệ thống");
            break;
          default:
            notification.setText(null);
            break;
        }
      }
      model.addAttribute("company", genericMapper.mapToType(company, InputCompanyDTO.class));
      model.addAttribute("avatar", company.getAvatar());
      model.addAttribute("notification", notification);
      return "company-info";
    }
    return "redirect:/techJob/login?text=account-not-found";
  }

  @PostMapping("/update-info")
  public String companyUpdate(
      @CookieValue(name = "company", defaultValue = "0") int company,
      @ModelAttribute InputCompanyDTO data) {
    if (companyService.updateCompany(company, data)) {
      return "redirect:/techJob/company";
    } else {
      return "redirect:/techJob/company?text=update-fail";
    }
  }

  @GetMapping("/get-form-create-job")
  public String getFormCreateJob(
      Model model,
      @ModelAttribute(name = "notification") NotificationRequest notification,
      @CookieValue(name = "company", defaultValue = "0") int companyId) {
    if (companyId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    OutputCompanyDTO company = companyService.findById(companyId);
    if (company != null) {
      if (notification.getText() != null) {
        switch (notification.getText()) {
          case "invalid-info":
            notification.setText(
                "Tạo tin tuyển dụng thất bại <br> Xin hãy nhập đầy đủ thông tin trước khi lưu");
            break;
          case "create-fail":
            notification.setText(
                "Tạo tin tuyển dụng thất bại <br> Thông tin công ty không có trong hệ thống");
            break;
          default:
            notification.setText(null);
            break;
        }
      }
      model.addAttribute("job", new InputJobDTO());
      model.addAttribute("notification", notification);
      model.addAttribute("avatar", company.getAvatar());
      model.addAttribute("listType", typeService.findAll());
      return "create-job";
    }
    return "redirect:/techJob/login?text=account-not-found";
  }

  @PostMapping("/create-job")
  public String createJob(
      @CookieValue(name = "company", defaultValue = "0") int companyId,
      @ModelAttribute InputJobDTO data) {
    if (Utils.isNullOrEmpty(data.getName())
        || Utils.isNullOrEmpty(String.valueOf(data.getNumberRecruit()))
        || Utils.isNullOrEmpty(data.getSalary())
        || Utils.isNullOrEmpty(data.getExperience())
        || Utils.isNullOrEmpty(data.getWorkingForm())
        || Utils.isNullOrEmpty(data.getDeadline().toString())
        || Utils.isNullOrEmpty(data.getWelfare())
        || Utils.isNullOrEmpty(data.getDescription())
        || Utils.isNullOrEmpty(data.getRequirement())) {
      return "redirect:/techJob/company/get-form-create-job?text=invalid-info";
    }
    Integer jobId = jobService.createJob(companyId, data);
    if (jobId != 0) {
      return "redirect:/techJob/job/" + jobId;
    } else {
      return "redirect:/techJob/company/get-form-create-job?text=create-fail";
    }
  }
}
