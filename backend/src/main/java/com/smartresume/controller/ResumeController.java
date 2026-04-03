package com.smartresume.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // allow frontend endpoints including Antigravity
public class ResumeController {

    @GetMapping("/job-titles")
    public List<String> getJobTitles() {
        return Arrays.asList(
                "Software Engineer",
                "Data Scientist",
                "Web Developer",
                "AI Engineer",
                "Backend Developer",
                "Frontend Developer");
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
        @RequestParam("files") MultipartFile[] files,
        @RequestParam(value = "jobTitle", required = false) String jobTitle,
        @RequestParam(value = "jobDescription", required = false) String jobDescription
    ) {

        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body("No files received");
        }

        List<Map<String, Object>> results = new ArrayList<>();

        for (MultipartFile file : files) {

            if (file.isEmpty()) continue;

            String name = file.getOriginalFilename();

            List<String> matched = Arrays.asList("java", "sql");
            List<String> missing = Arrays.asList("react");

            int score = (matched.size() * 100) / (matched.size() + missing.size());

            Map<String, Object> result = new HashMap<>();
            result.put("name", name);
            result.put("score", score);
            result.put("matchedSkills", matched);
            result.put("missingSkills", missing);
            result.put("suggestions", missing);

            results.add(result);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("resumes", results);
        response.put("totalResumes", results.size());
        response.put("jobTitle", jobTitle);
        return org.springframework.http.ResponseEntity.ok(response);
    }
}