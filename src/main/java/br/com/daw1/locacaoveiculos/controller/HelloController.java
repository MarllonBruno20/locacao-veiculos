package br.com.daw1.locacaoveiculos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
