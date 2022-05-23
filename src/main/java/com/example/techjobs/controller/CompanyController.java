package com.example.techjobs.controller;

import com.example.techjobs.dto.inputDTO.InputCompanyDTO;
import com.example.techjobs.dto.outputDTO.OutputCompanyDTO;
import com.example.techjobs.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
public class CompanyController {

  private final CompanyService companyService;

  @Autowired
  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  @GetMapping("/{id}")
  public String companyFindById(@PathVariable(name = "id") Integer companyId) {
    OutputCompanyDTO company = companyService.findById(companyId);
    return "";
  }

  @PostMapping("/create")
  public String companyCreate(@ModelAttribute InputCompanyDTO data) {
    companyService.createCompany(data);
    return "";
  }

  @PostMapping("/{id}/update")
  public String companyUpdate(
      @PathVariable(name = "id") Integer companyId, @ModelAttribute InputCompanyDTO data) {
    companyService.updateCompany(companyId, data);
    return "";
  }
}
