package com.example.techjobs.controller.admin;

import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.entity.Type;
import com.example.techjobs.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/techJob/admin/types")
public class AdminTypeController {

  private final TypeService typeService;

  @Autowired
  public AdminTypeController(TypeService typeService) {
    this.typeService = typeService;
  }

  @GetMapping
  public String index(
      Model model, @CookieValue(name = "admin", defaultValue = "-1") Integer adminId) {
    if (adminId != 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    model.addAttribute("types", this.typeService.findAll());
    return "admin/type/index";
  }

  @PostMapping
  public String onCreate(
      Type type,
      RedirectAttributes redirectAttributes,
      @CookieValue(name = "admin", defaultValue = "-1") Integer adminId) {
    if (adminId != 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    ResultDTO<Type> result = this.typeService.save(type);
    if (result.isError()) {
      redirectAttributes.addFlashAttribute("error", result.getMessage());
    } else {
      redirectAttributes.addFlashAttribute("success", result.getMessage());
    }
    return "redirect:/techJob/admin/types";
  }

  @GetMapping("/delete/{id}")
  public String onDelete(
      @PathVariable Integer id,
      RedirectAttributes redirectAttributes,
      @CookieValue(name = "admin", defaultValue = "-1") Integer adminId) {
    if (adminId != 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    ResultDTO<Type> result = this.typeService.delete(id);
    if (result.isError()) {
      redirectAttributes.addFlashAttribute("error", result.getMessage());
    } else {
      redirectAttributes.addFlashAttribute("success", result.getMessage());
    }
    return "redirect:/techJob/admin/types";
  }
}
