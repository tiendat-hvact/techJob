package com.example.techjobs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/")
  public String redirectToHome() {
    return "redirect:/techJob";
  }
}
