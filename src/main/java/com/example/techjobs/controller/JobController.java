package com.example.techjobs.controller;

import com.example.techjobs.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/techJob/job")
public class JobController {

    private final JobService jobService;

    @GetMapping("/job-following")
    public String getJobFollowing(Model model, @CookieValue("user") Integer userId) {
        model.addAttribute("jobs", this.jobService.getJobFollowing(userId));
        return "job-following";
    }

    @GetMapping("/follow/{id}")
    public String followJob(@PathVariable Integer id, @CookieValue("user") Integer userId, @RequestParam String returnUrl) {
        this.jobService.followJob(id, userId);
        return "redirect:"+returnUrl;
    }

    @GetMapping("/unfollow/{id}")
    public String unfollowJob(@PathVariable Integer id, @CookieValue("user") Integer userId, @RequestParam String returnUrl) {
        this.jobService.unfollowJob(id, userId);
        return "redirect:"+returnUrl;
    }
}