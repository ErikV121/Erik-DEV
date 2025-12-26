package com.erikv121.controller;

import com.erikv121.email.EmailDto;
import com.erikv121.email.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final EmailService emailService;

    public HomeController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }


    @PostMapping("/sendMail")
    public String sendMail(@ModelAttribute EmailDto emailDto, RedirectAttributes redirectAttributes) {
        try {
            emailService.sendSimpleMail(emailDto);
            redirectAttributes.addFlashAttribute("message", "Mail Sent Successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
        }
        return "redirect:/#contact";
    }


    @GetMapping("/other")
    public String other(Model model) {
        model.addAttribute("year", java.time.Year.now().getValue());
        return "other";
    }

}
