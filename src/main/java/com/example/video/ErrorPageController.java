package com.example.video;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorPageController {



    @GetMapping("/error")
    public String errorPage() {
        return "error/error";
    }

}

