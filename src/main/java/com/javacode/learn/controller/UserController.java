package com.javacode.learn.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @GetMapping("/user")
    public String user(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("name", principal.getAttribute("name"));
        model.addAttribute("login", principal.getAttribute("login"));
        model.addAttribute("email", getEmailFromGitHub(principal));
        return "user";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Возвращает имя Thymeleaf-шаблона (login.html)
    }

    /*@PostMapping("/logout")
    public String logout() {
        return "logout"; // Возвращает имя Thymeleaf-шаблона (login.html)
    }*/

    private String getEmailFromGitHub(OAuth2User principal) {
        return (String) principal.getAttribute("email");
    }
}
