package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.model.Usuario;
import br.com.daw1.locacaoveiculos.model.enums.TipoUsuario;
import br.com.daw1.locacaoveiculos.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    // A única responsabilidade deste controller é exibir a página de login.
    @GetMapping("/login")
    public String showLoginForm() {
        return "public/login"; // Retorna o caminho para seu arquivo login.html
    }
}
