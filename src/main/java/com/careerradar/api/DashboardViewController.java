package com.careerradar.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardViewController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
