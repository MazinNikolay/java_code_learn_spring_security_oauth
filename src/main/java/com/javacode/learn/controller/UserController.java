package com.javacode.learn.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/app")
public class UserController {
    @GetMapping("/user")
    public String user(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("name", principal.getAttribute("name"));
        model.addAttribute("login", principal.getAttribute("login"));
        model.addAttribute("email", getEmailFromGitHub(principal));
        return "user";
    }

    private String getEmailFromGitHub(OAuth2User principal) {
        List<Map<String, Object>> emails = principal.getAttribute("emails");
        if (emails != null && !emails.isEmpty()) {
            return (String) emails.get(0).get("email");
        }
        return null;
    }
}
