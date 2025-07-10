package br.com.daw1.locacaoveiculos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

     // --- MÉTODO PARA TESTAR ERRO 500 ----------------------------------------
     //acesse a URL: http://localhost:8080/trigger-500
    // @GetMapping("/trigger-500")
    // public String triggerInternalServerError() {
    //     throw new RuntimeException("Este é um erro 500 forçado para teste!");
    // }
    // --------------------------------------------------------------------------

    // --- MÉTODO PARA TESTAR ERRO GENÉRIC0---
    //http://localhost:8080/trigger-error-generic?number=abc
    //@GetMapping("/trigger-error-generic")
    //public String triggerGenericError(@RequestParam int number) {
        // Se você tentar acessar /trigger-error-generic?number=abc,
        // o Spring não conseguirá converter "abc" para um int,
        // resultando em um erro 400 (Bad Request), que deve acionar o error.html
        //return "Número recebido: " + number; // Esta linha não será alcançada em caso de erro
    //}
    // ----------------------------------------------------------------
}
