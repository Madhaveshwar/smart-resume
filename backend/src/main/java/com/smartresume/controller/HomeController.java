package com.smartresume.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")  // ✅ ADD THIS LINE
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Smart Resume Backend is Running 🚀";
    }
}