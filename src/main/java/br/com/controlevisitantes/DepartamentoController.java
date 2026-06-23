package br.com.controlevisitantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService;

    // ── Listar ───────────────────────────────────────────────────
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("departamentos", departamentoService.listar());
        return "departamento"; // → templates/departamento.html
    }

    // ── Cadastrar ────────────────────────────────────────────────
    @PostMapping("/cadastrar")
    public String cadastrar(@RequestParam String nome, RedirectAttributes ra) {
        boolean ok = departamentoService.cadastrar(nome);
        if (ok) {
            ra.addFlashAttribute("sucesso", "Departamento cadastrado com sucesso!");
        } else {
            ra.addFlashAttribute("erro", "Departamento já cadastrado ou nome inválido.");
        }
        return "redirect:/departamentos";
    }

    // ── Deletar ──────────────────────────────────────────────────
    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable int id, RedirectAttributes ra) {
        boolean ok = departamentoService.deletar(id);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Departamento removido com sucesso!" : "Erro ao remover departamento.");
        return "redirect:/departamentos";
    }

    // ── Renomear ─────────────────────────────────────────────────
    @PostMapping("/renomear/{id}")
    public String renomear(@PathVariable int id,
                           @RequestParam String novoNome,
                           RedirectAttributes ra) {
        boolean ok = departamentoService.renomear(id, novoNome);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Departamento renomeado com sucesso!" : "Erro ao renomear departamento.");
        return "redirect:/departamentos";
    }
}
