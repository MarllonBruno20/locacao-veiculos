package br.com.daw1.locacaoveiculos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "public/login"; // Certifique-se que o caminho da sua view de login está correto
    }
}