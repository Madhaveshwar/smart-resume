package com.smartresume.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")   // ✅ VERY IMPORTANT
public class ResumeController {

    @GetMapping("/api/job-titles")
    public List<String> getJobTitles() {
        return List.of(
            "Software Engineer",
            "Data Scientist",
            "Web Developer",
            "AI Engineer",
            "DevOps Engineer"
        );
    }
}