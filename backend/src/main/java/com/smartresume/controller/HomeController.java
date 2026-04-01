package com.smartresume.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;  // ✅ IMPORTANT

@RestController
public class HomeController {

    // ✅ Test API (optional but good)
    @GetMapping("/")
    public String home() {
        return "Smart Resume Backend is Running 🚀";
    }

    // ✅ Job Roles API (THIS FIXES YOUR DROPDOWN)
    @GetMapping("/api/job-roles")
    public List<String> getJobRoles() {
        return List.of(
            "Software Engineer",
            "Data Scientist",
            "Web Developer",
            "AI Engineer",
            "DevOps Engineer"
        );
    }
}