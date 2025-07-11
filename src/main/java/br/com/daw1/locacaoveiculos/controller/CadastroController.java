package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Pessoa;
import br.com.daw1.locacaoveiculos.model.Usuario;
import br.com.daw1.locacaoveiculos.service.UsuarioService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cadastro") // Rota pública
public class CadastroController {

    private final UsuarioService usuarioService;

    public CadastroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @HxRequest
    @GetMapping
    public String mostrarFormularioDeCadastroPublico(Model model) {
        Usuario usuario = new Usuario();
        usuario.setPessoa(new Pessoa()); // Importante para o th:field="*{pessoa.nome}" funcionar
        model.addAttribute("usuario", usuario);

        // Flag para o Thymeleaf saber que NÃO deve mostrar o campo de tipo de usuário
        model.addAttribute("isAdminContext", false);

        return "public/cadastro :: conteudo";
    }

    @HxRequest
    @PostMapping
    public String processarCadastroPublico(@Valid @ModelAttribute("usuario") Usuario usuario,
                                           BindingResult bindingResult,
                                           Model model, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            // Se houver erros, retorna para a mesma tela de cadastro
            model.addAttribute("isAdminContext", false);
            return "public/cadastro";
        }

        try {
            // Chama o novo método no service que força o papel de CLIENTE
            usuarioService.registrarNovoCliente(usuario);
        } catch (RegraNegocioException e) {
            // Se o usuário ou CPF já existir, por exemplo
            model.addAttribute("erroCadastro", e.getMessage());
            model.addAttribute("isAdminContext", false);
            return "public/cadastro";
        }

        // --- AQUI ESTÁ A LÓGICA DE REDIRECIONAMENTO PARA HTMX ---
        // Em vez de "return redirect:/...", nós definimos o header HX-Redirect.
        response.setHeader("HX-Redirect", "/login?cadastro=sucesso");

        // Retorna um corpo vazio, pois o navegador fará o redirect.
        return "";
    }
}