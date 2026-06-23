package br.com.controlevisitantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/visitantes")
public class VisitanteController {

    @Autowired
    private VisitanteService visitanteService;

    // ── Listar ───────────────────────────────────────────────────
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("visitantes", visitanteService.listar());
        return "visitante"; // → templates/visitante.html
    }

    // ── Cadastrar ────────────────────────────────────────────────
    @PostMapping("/cadastrar")
    public String cadastrar(@RequestParam String nome,
                            @RequestParam String cpf,
                            @RequestParam String telefone,
                            @RequestParam String empresa,
                            RedirectAttributes ra) {
        boolean ok = visitanteService.cadastrar(nome, cpf, telefone, empresa);
        if (ok) {
            ra.addFlashAttribute("sucesso", "Visitante cadastrado com sucesso!");
        } else {
            ra.addFlashAttribute("erro", "CPF já cadastrado ou dados inválidos.");
        }
        return "redirect:/visitantes";
    }

    // ── Deletar ──────────────────────────────────────────────────
    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable int id, RedirectAttributes ra) {
        boolean ok = visitanteService.deletar(id);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Visitante removido com sucesso!" : "Erro ao remover visitante.");
        return "redirect:/visitantes";
    }

    // ── Alterar ──────────────────────────────────────────────────
    @PostMapping("/alterar/{id}")
    public String alterar(@PathVariable int id,
                          @RequestParam String nome,
                          @RequestParam String cpf,
                          @RequestParam String telefone,
                          @RequestParam String empresa,
                          RedirectAttributes ra) {
        boolean ok = visitanteService.alterar(id, nome, cpf, telefone, empresa);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Visitante atualizado com sucesso!" : "Erro ao atualizar visitante.");
        return "redirect:/visitantes";
    }
}
