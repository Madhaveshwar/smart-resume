package com.smartresume.controller;

import com.smartresume.model.Resume;
import com.smartresume.service.ResumeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ResumeController {

    // Create a Spring Boot controller for ATS system.
    // Endpoints required:
    // GET "/" -> show upload page with job titles
    // POST "/upload" -> process resumes and return results
    // GET "/dashboard" -> show all resumes sorted by matchPercentage
    // GET "/search?keyword=" -> filter resumes by keyword
    // POST "/clear" -> delete all resumes
    // Ensure model contains:
    // - resumes list
    // - mode (upload, results, dashboard, search)

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("mode", "upload");
        model.addAttribute("jobTitles", resumeService.getJobTitles());
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("pdf_docs") List<MultipartFile> files,
                         @RequestParam("job_title") String jobTitle,
                         @RequestParam(value = "job_desc", required = false) String jobDescription,
                         Model model) {
        List<Resume> resumes = resumeService.processAndSave(files, jobTitle, jobDescription);

        model.addAttribute("mode", "results");
        model.addAttribute("jobTitles", resumeService.getJobTitles());
        model.addAttribute("jobTitle", jobTitle);
        model.addAttribute("resumes", resumes);
        model.addAttribute("totalProcessed", resumes.size());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Resume> resumes = resumeService.getAllResumes();
        model.addAttribute("mode", "dashboard");
        model.addAttribute("resumes", resumes);
        model.addAttribute("totalStored", resumes.size());
        model.addAttribute("jobTitles", resumeService.getJobTitles());
        return "index";
    }

    @GetMapping("/search")
    public String searchPage(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Resume> resumes = resumeService.searchByKeyword(keyword);

        model.addAttribute("mode", "search");
        model.addAttribute("keyword", keyword == null ? "" : keyword.trim());
        model.addAttribute("resumes", resumes);
        model.addAttribute("jobTitles", resumeService.getJobTitles());
        return "index";
    }

    @PostMapping("/clear")
    public String clearAll() {
        resumeService.clearAll();
        return "redirect:/dashboard";
    }

    @GetMapping("/api/job-titles")
    @ResponseBody
    public List<String> apiJobTitles() {
        return resumeService.getJobTitles();
    }

    @PostMapping("/api/process")
    @ResponseBody
    public Map<String, Object> apiProcess(@RequestParam("pdf_docs") List<MultipartFile> files,
                                          @RequestParam("job_title") String jobTitle,
                                          @RequestParam(value = "job_desc", required = false) String jobDescription) {
        List<Resume> resumes = resumeService.processAndSave(files, jobTitle, jobDescription);
        Map<String, Object> response = new HashMap<>();
        response.put("jobTitle", jobTitle);
        response.put("resumes", resumes);
        response.put("totalProcessed", resumes.size());
        return response;
    }

    @GetMapping("/api/search")
    @ResponseBody
    public List<Resume> apiSearch(@RequestParam(value = "keyword", required = false) String keyword) {
        return resumeService.searchByKeyword(keyword);
    }

    @GetMapping("/api/dashboard")
    @ResponseBody
    public Map<String, Object> apiDashboard() {
        List<Resume> resumes = resumeService.getAllResumes();
        Map<String, Object> response = new HashMap<>();
        response.put("totalStored", resumes.size());
        response.put("resumes", resumes);
        return response;
    }

    @PostMapping("/api/clear")
    @ResponseBody
    public Map<String, Object> apiClear() {
        resumeService.clearAll();
        return Collections.singletonMap("status", "cleared");
    }
}
