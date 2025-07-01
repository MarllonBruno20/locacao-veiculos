package br.com.daw1.locacaoveiculos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "public/login"; // Você precisará criar esta view
    }

    // Em uma aplicação Spring Security, o POST de login é geralmente tratado pelo próprio Spring Security
    // Você não precisa de um @PostMapping para /login aqui, a menos que esteja implementando a segurança manualmente.
}