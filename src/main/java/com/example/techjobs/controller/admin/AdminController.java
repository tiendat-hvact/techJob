package com.example.techjobs.controller.admin;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/techJob/admin")
public class AdminController {

  private final GenericMapper genericMapper;
  private final UserService userService;

  @Autowired
  public AdminController(GenericMapper genericMapper, UserService userService) {
    this.genericMapper = genericMapper;
    this.userService = userService;
  }

  @GetMapping
  public String index(@CookieValue(name = "admin", defaultValue = "-1") Integer adminId) {
    if (adminId != 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    return "admin/dashboard";
  }

  @PostMapping("/update-info")
  public String userUpdate(
      @CookieValue(name = "admin") Integer adminId, @ModelAttribute InputUserDTO data) {
    if (userService.updateUser(adminId, data)) {
      return "redirect:/techJob/admin";
    } else {
      return "redirect:/techJob/admin?text=update-fail";
    }
  }
}
