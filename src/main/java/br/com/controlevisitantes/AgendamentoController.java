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
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private JdbcTemplate jdbc;

    // ── Listar ───────────────────────────────────────────────────
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("agendamentos",  agendamentoService.listar());
        model.addAttribute("proximos",      agendamentoService.listarProximos());
        model.addAttribute("departamentos", agendamentoService.listarDepartamentos());
        model.addAttribute("visitantes",    agendamentoService.listarVisitantes());
        return "agendamento";
    }

    // ── Criar ────────────────────────────────────────────────────
    @PostMapping("/criar")
    public String criar(@RequestParam int departamentoId,
                        @RequestParam String dataAgendamento,
                        @RequestParam String motivo,
                        @RequestParam List<Integer> visitantesIds,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes ra) {

        int usuarioId = jdbc.queryForObject(
            "SELECT id FROM usuarios WHERE login = ?", Integer.class, userDetails.getUsername()
        );

        boolean ok = agendamentoService.criar(departamentoId, usuarioId, dataAgendamento, null, motivo, visitantesIds);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Agendamento criado com sucesso!" : "Erro ao criar agendamento.");
        return "redirect:/agendamentos";
    }

    // ── Confirmar chegada ─────────────────────────────────────────
    @PostMapping("/confirmar/{id}")
    public String confirmar(@PathVariable int id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes ra) {

        int usuarioId = jdbc.queryForObject(
            "SELECT id FROM usuarios WHERE login = ?", Integer.class, userDetails.getUsername()
        );

        boolean ok = agendamentoService.confirmarChegada(id, usuarioId);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Chegada confirmada! Visita registrada com sucesso." : "Agendamento não encontrado ou já processado.");
        return "redirect:/agendamentos";
    }

    // ── Cancelar ─────────────────────────────────────────────────
    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable int id, RedirectAttributes ra) {
        boolean ok = agendamentoService.cancelar(id);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Agendamento cancelado." : "Não foi possível cancelar.");
        return "redirect:/agendamentos";
    }

    // ── Deletar ──────────────────────────────────────────────────
    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable int id, RedirectAttributes ra) {
        boolean ok = agendamentoService.deletar(id);
        ra.addFlashAttribute(ok ? "sucesso" : "erro",
                ok ? "Agendamento removido." : "Erro ao remover.");
        return "redirect:/agendamentos";
    }
}
