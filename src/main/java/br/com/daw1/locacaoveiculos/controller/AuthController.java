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
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "public/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Usuario user = usuarioService.autenticar(username, password);

        if (user != null) {
            session.setAttribute("usuarioLogado", user);

            // Redireciona baseado no tipo do usuário
            if (user.getTipo() == TipoUsuario.ADMINISTRADOR) {
                return "redirect:/admin";
            } else {
                return "redirect:/cliente";
            }
        } else {
            model.addAttribute("erro", "Usuário ou senha inválidos.");
            return "public/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // @GetMapping("/logout")
    // public String logout(HttpSession session) {
    //     session.invalidate();
    //     return "redirect:/";
    // }

}
