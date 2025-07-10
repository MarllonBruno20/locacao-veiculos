package br.com.daw1.locacaoveiculos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.daw1.locacaoveiculos.model.Usuario;
import br.com.daw1.locacaoveiculos.model.enums.TipoUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException; // Adicione esta importação

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String adminDashboard(HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getTipo() != TipoUsuario.ADMINISTRADOR) {
            return "redirect:/auth/login";
        }

        return "admin/dashboard"; // sua página inicial do admin
    }

    //No navegador, acesse a URL: http://localhost:8080/admin/trigger-403
    // --- MÉTODO PARA TESTAR ERRO 403---
    //@GetMapping("/trigger-403")
    //public String triggerForbiddenError(HttpSession session) {
        // Para simular o 403, vamos negar o acesso se o usuário for ADMIN.
        // Em um cenário real, isso seria tratado pelo Spring Security com regras de permissão.
        //Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        //if (logado != null && logado.getTipo() == TipoUsuario.ADMINISTRADOR) {
        //     throw new AccessDeniedException("Você é um ADMIN, mas este recurso é restrito para outros testes. Acesso Negado!");
        //}
        // Se não for ADMIN (ex: CLIENTE ou não logado), você pode retornar algo ou redirecionar.
        // Para garantir que a página 403 seja vista, o lançamento da exceção é o caminho direto.
        //return "redirect:/"; // Redireciona se não for ADMIN (não verá o 403 se a exceção não for lançada)
   // }
    // --------------------------------------------------------
}