package com.smartresume.service;

import com.smartresume.model.Resume;
import com.smartresume.repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeService {

    // Ensure resume processing stores complete ATS analysis.
    // For each resume:
    // - Extract text using Tika
    // - Call matchingService.analyseResume()
    // - Save resume in database
    // - Ensure fields like:
    //   matchPercentage, skillsMatch, keywordMatch, experienceMatch,
    //   matchedSkills, missingSkills, suggestions,
    //   whyScore, profileSummary, hiringDecision
    // are fully populated before saving.

    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);

    private final MatchingService matchingService;
    private final ResumeRepository resumeRepository;

    @Autowired
    public ResumeService(MatchingService matchingService, ResumeRepository resumeRepository) {
        this.matchingService = matchingService;
        this.resumeRepository = resumeRepository;
    }

    public List<Resume> processAndSave(List<MultipartFile> files,
            String jobTitle,
            String jobDescription) {
        List<Resume> results = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // ✅ Using simple text extraction
                String text = new String(file.getBytes());

                if (text.isBlank())
                    continue;

                Resume resume = buildResume(
                        file.getOriginalFilename(),
                        text,
                        jobTitle,
                        jobDescription);

                resumeRepository.save(resume);
                System.out.println("Saved Resume: " + resume.getFileName());
                
                results.add(resume);

            } catch (Exception e) {
                logger.error("Error processing file {}", file.getOriginalFilename(), e);
            }
        }

        results.sort(Comparator.comparingInt(Resume::getMatchPercentage).reversed());
        return results;
    }

    public List<Resume> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return resumeRepository.findAllByOrderByMatchPercentageDesc();
        }

        return resumeRepository.searchByKeyword(keyword.trim());
    }

    public List<Resume> getAllResumes() {
        return resumeRepository.findAllByOrderByMatchPercentageDesc();
    }

    public void clearAll() {
        resumeRepository.deleteAll();
    }

    public List<String> getJobTitles() {
        return matchingService.getJobTitles();
    }

    // ── Build resume ─────────────────────────
    private Resume buildResume(String fileName, String text,
            String jobTitle, String jobDescription) {

        Resume r = new Resume();
        r.setFileName(fileName);
        r.setRawText(text);
        r.setName(extractName(text));
        r.setEmail(extractEmail(text));
        r.setPhone(extractPhone(text));

        // Delegate all skill detection, matching, scoring, and suggestions
        // to the upgraded MatchingService
        matchingService.analyseResume(r, jobTitle, jobDescription);

        logger.info("--- Resume Analysis For: {} ---", r.getFileName());
        logger.info("Overall Match Percentage: {}", r.getMatchPercentage());
        logger.info("Skills Match Breakdown: {}", r.getSkillsMatch());
        logger.info("Keyword Match Breakdown: {}", r.getKeywordMatch());
        logger.info("Experience Match Breakdown: {}", r.getExperienceMatch());
        logger.info("Suggestions: {}", r.getSuggestions());

        return r;
    }

    // ── Helpers ─────────────────────────
    private String extractName(String text) {
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            String[] words = line.split("\\s+");
            if (words.length >= 2 && words.length <= 5 &&
                    Character.isUpperCase(words[0].charAt(0))) {
                return line;
            }
        }
        return "Unknown";
    }

    private String extractEmail(String text) {
        Matcher m = Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}")
                .matcher(text);
        return m.find() ? m.group() : "Not found";
    }

    private String extractPhone(String text) {
        Matcher m = Pattern.compile("(\\+?\\d{1,3}[\\s\\-]?)?(\\(?\\d{3}\\)?[\\s\\-]?)?[\\d\\s\\-]{7,13}")
                .matcher(text);
        return m.find() ? m.group().trim() : "Not found";
    }
}