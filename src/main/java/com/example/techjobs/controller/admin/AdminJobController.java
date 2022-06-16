package com.example.techjobs.controller.admin;

import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/techJob/admin/jobs")
public class AdminJobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    public String index(Model model, @CookieValue(name = "admin", defaultValue = "-1") Integer adminId) {
        if (adminId != 0) {
            return "redirect:/techJob/login?text=unauthorized";
        }
        model.addAttribute("jobs", this.jobService.findAll());
        return "admin/jobs/index";
    }

    @GetMapping("/delete/{id}")
    public String onDelete(@PathVariable Integer id, RedirectAttributes redirectAttributes, @CookieValue(name = "admin", defaultValue = "-1") Integer adminId) {
        if (adminId != 0) {
            return "redirect:/techJob/login?text=unauthorized";
        }
        ResultDTO result = this.jobService.delete(id);
        if (result.isError()) {
            redirectAttributes.addFlashAttribute("error", result.getMessage());
        } else {
            redirectAttributes.addFlashAttribute("success", result.getMessage());
        }
        return "redirect:/techJob/admin/jobs";
    }

}