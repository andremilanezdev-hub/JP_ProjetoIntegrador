package br.com.controlevisitantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/visitas")
public class VisitaController {

    @Autowired
    private VisitaService visitaService;

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ── Listar ───────────────────────────────────────────────────
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("visitas",           visitaService.listar());
        model.addAttribute("emAberto",          visitaService.listarEmAberto());
        model.addAttribute("departamentos",     visitaService.listarDepartamentos());
        model.addAttribute("visitantes",        visitaService.listarVisitantes());
        model.addAttribute("agendamentosHoje",  agendamentoService.listarHoje());
        return "visita";
    }

    // ── Check-in: confirma chegada do agendamento e redireciona para /visitas ──
    @PostMapping("/checkin/{id}")
    public String checkin(@PathVariable int id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes ra) {
        int usuarioId = jdbcTemplate.queryForObject(
            "SELECT id FROM usuarios WHERE login = ?", Integer.class, userDetails.getUsername()
        );
        boolean ok = agendamentoService.confirmarChegada(id, usuarioId);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Check-in realizado! Visita registrada." : "Agendamento não encontrado ou já processado.");
        return "redirect:/visitas";
    }

    // ── Registrar entrada ─────────────────────────────────────────
    @PostMapping("/entrada")
    public String registrarEntrada(
            @RequestParam int departamentoId,
            @RequestParam String motivo,
            @RequestParam List<Integer> visitantesIds,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes ra) {

        // Busca o id do usuário logado
        int usuarioId = jdbcTemplate.queryForObject(
            "SELECT id FROM usuarios WHERE login = ?", Integer.class, userDetails.getUsername()
        );

        boolean ok = visitaService.registrarEntrada(departamentoId, usuarioId, motivo, visitantesIds);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Entrada registrada com sucesso!" : "Um ou mais visitantes já possuem visita em andamento.");
        return "redirect:/visitas";
    }

    // ── Registrar saída ───────────────────────────────────────────
    @PostMapping("/saida/{id}")
    public String registrarSaida(@PathVariable int id, RedirectAttributes ra) {
        boolean ok = visitaService.registrarSaida(id);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Saída registrada com sucesso!" : "Saída já registrada ou visita não encontrada.");
        return "redirect:/visitas";
    }

    // ── Deletar ──────────────────────────────────────────────────
    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable int id, RedirectAttributes ra) {
        boolean ok = visitaService.deletar(id);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Visita removida com sucesso!" : "Erro ao remover visita.");
        return "redirect:/visitas";
    }
}
