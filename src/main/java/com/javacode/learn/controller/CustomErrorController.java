package com.javacode.learn.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            logger.error("Error occurred. Status code: {}", statusCode);
            switch (statusCode) {
                case 401:
                    return "error-401"; // Страница для ошибки 401
                case 403:
                    return "error-403"; // Страница для ошибки 403
                case 404:
                    return "error-404"; // Страница для ошибки 404
                default:
                    return "error"; // Общая страница ошибки
            }
        }
        return "error";
    }

    public String getErrorPath() {
        return "/error";
    }
}
