package br.com.controlevisitantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ── Listar ───────────────────────────────────────────────────
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuario"; // → templates/usuario.html
    }

    // ── Cadastrar ────────────────────────────────────────────────
    @PostMapping("/cadastrar")
    public String cadastrar(@RequestParam String nome,
                            @RequestParam String login,
                            @RequestParam String senha,
                            RedirectAttributes ra) {
        boolean ok = usuarioService.cadastrar(nome, login, senha);
        if (ok) {
            ra.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso!");
        } else {
            ra.addFlashAttribute("erro", "Login ou nome já cadastrado.");
        }
        return "redirect:/usuarios";
    }

    // ── Deletar ──────────────────────────────────────────────────
    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable int id, RedirectAttributes ra) {
        boolean ok = usuarioService.deletar(id);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Usuário removido com sucesso!" : "Erro ao remover usuário.");
        return "redirect:/usuarios";
    }

    // ── Alterar Senha ────────────────────────────────────────────
    @PostMapping("/senha/{id}")
    public String alterarSenha(@PathVariable int id,
                               @RequestParam String novaSenha,
                               RedirectAttributes ra) {
        boolean ok = usuarioService.alterarSenha(id, novaSenha);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Senha alterada com sucesso!" : "Erro ao alterar senha.");
        return "redirect:/usuarios";
    }
}
