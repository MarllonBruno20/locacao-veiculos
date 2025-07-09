package br.com.daw1.locacaoveiculos.controller;

import br.com.daw1.locacaoveiculos.exception.RegraNegocioException;
import br.com.daw1.locacaoveiculos.model.Pessoa;
import br.com.daw1.locacaoveiculos.model.Usuario;
import br.com.daw1.locacaoveiculos.model.enums.TipoUsuario;
import br.com.daw1.locacaoveiculos.service.PessoaService;
import br.com.daw1.locacaoveiculos.service.UsuarioService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse; // Importação adicionada
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional; // Importação adicionada

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PessoaService pessoaService;

    //@GetMapping
    //public String listarUsuarios(Model model) {
    //    model.addAttribute("usuarios", usuarioService.listarTodos());
    //    model.addAttribute("titulo", "Gerenciar Usuários");
    //    return "admin/lista_usuarios";
    //}


    @GetMapping
    public String listarUsuarios(HttpSession session, Model model) {
    Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getTipo() != TipoUsuario.ADMINISTRADOR) {
            return "redirect:/auth/login";
        }

        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("titulo", "Gerenciar Usuários");
        return "admin/lista_usuarios";
    }


    @HxRequest
    @GetMapping("/novo")
    public String mostrarFormularioCadastroUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("pessoa", new Pessoa());
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        return "admin/cadastrar_usuario :: formularioUsuario";
    }

    @HxRequest
@PostMapping("/salvar")
public String salvarUsuario(
        @Valid @ModelAttribute("usuario") Usuario usuario,
        BindingResult bindingResult,
        Model model) {

    if (bindingResult.hasErrors()) {
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        return "admin/cadastrar_usuario :: formularioUsuario";
    }

    try {
        Pessoa pessoaParaSalvar = usuario.getPessoa();
        Pessoa pessoaSalva = pessoaService.salvar(pessoaParaSalvar);

        usuario.setPessoa(pessoaSalva);
        usuarioService.salvar(usuario);

    } catch (RegraNegocioException e) {
        if (e.getMessage().toLowerCase().contains("cpf")) {
            bindingResult.rejectValue("pessoa.cpf", "error.pessoa", e.getMessage());
        } else {
            bindingResult.rejectValue("nomeUsuario", "error.usuario", e.getMessage());
        }
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        return "admin/cadastrar_usuario :: formularioUsuario";
    }

    // ✅ Redireciona normalmente para carregar CSS e scripts
    return "redirect:/admin/usuarios";
}

    // Métodos de edição e exclusão (já existentes ou a serem adicionados)
    @HxRequest
    @GetMapping("/editar/{codigo}")
    public String mostrarFormularioEdicaoUsuario(@PathVariable Long codigo, Model model) {
        Usuario usuario = usuarioService.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado - Código: " + codigo));
        model.addAttribute("usuario", usuario);
        model.addAttribute("pessoa", usuario.getPessoa()); // Carrega a pessoa associada
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        return "admin/formulario_edicao_usuario :: formularioUsuarioEdicao"; // Você precisará criar esta view
    }

    @HxRequest
    @PostMapping("/deletar/{codigo}")
    public String deletarUsuario(@PathVariable Long codigo, HttpServletResponse response) {
        usuarioService.deletar(codigo);
        response.setHeader("HX-Trigger", "usuarioDeletado"); // Evento para HTMX atualizar a lista
        return ""; // Ou um fragmento vazio
    }
}