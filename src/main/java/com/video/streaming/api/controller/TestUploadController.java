package com.video.streaming.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestUploadController {

    // For testing purposes only
    @GetMapping("/page-upload")
    public ModelAndView getPageUpload() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("file-upload-test.html");
        return modelAndView;
    }
}