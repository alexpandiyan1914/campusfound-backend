package com.campusfound.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public String student() {
        return "Welcome Student";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "Welcome Admin";
    }

    @GetMapping("/public")
    public String publicApi() {
        return "Public API";
    }
}