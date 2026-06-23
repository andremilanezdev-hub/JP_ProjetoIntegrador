package br.com.controlevisitantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Service
public class AgendamentoService {

    @Autowired
    private JdbcTemplate jdbc;

    // ── Listar todos ─────────────────────────────────────────────
    public List<Map<String, Object>> listar() {
        String sql = """
            SELECT
                a.id,
                a.data_agendamento,
                a.motivo,
                a.status,
                d.nome  AS departamento,
                u.nome  AS usuario,
                GROUP_CONCAT(v.nome SEPARATOR ', ') AS visitantes
            FROM agendamentos a
            LEFT JOIN departamentos d  ON d.id = a.departamento_id
            LEFT JOIN usuarios u       ON u.id = a.usuario_id
            LEFT JOIN agendamentos_visitantes av ON av.agendamento_id = a.id
            LEFT JOIN visitantes v      ON v.id = av.visitante_id
            GROUP BY a.id
            ORDER BY a.data_agendamento DESC
            """;
        return jdbc.queryForList(sql);
    }

    // ── Listar agendamentos do dia (e próximos 7 dias) ────────────
    public List<Map<String, Object>> listarProximos() {
        String sql = """
            SELECT
                a.id,
                a.data_agendamento,
                a.motivo,
                a.status,
                d.nome AS departamento,
                GROUP_CONCAT(v.nome SEPARATOR ', ') AS visitantes
            FROM agendamentos a
            LEFT JOIN departamentos d  ON d.id = a.departamento_id
            LEFT JOIN agendamentos_visitantes av ON av.agendamento_id = a.id
            LEFT JOIN visitantes v ON v.id = av.visitante_id
            WHERE a.status = 'AGENDADO'
              AND DATE(a.data_agendamento) BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
            GROUP BY a.id
            ORDER BY a.data_agendamento ASC
            """;
        return jdbc.queryForList(sql);
    }

    // ── Agendamentos de hoje (status AGENDADO) ────────────────────
    public List<Map<String, Object>> listarHoje() {
        String sql = """
            SELECT
                a.id,
                a.data_agendamento,
                a.motivo,
                d.nome  AS departamento,
                u.nome  AS usuario,
                GROUP_CONCAT(v.nome SEPARATOR ', ') AS visitantes
            FROM agendamentos a
            LEFT JOIN departamentos d  ON d.id = a.departamento_id
            LEFT JOIN usuarios u       ON u.id = a.usuario_id
            LEFT JOIN agendamentos_visitantes av ON av.agendamento_id = a.id
            LEFT JOIN visitantes v      ON v.id = av.visitante_id
            WHERE a.status = 'AGENDADO'
              AND DATE(a.data_agendamento) = CURDATE()
            GROUP BY a.id
            ORDER BY a.data_agendamento ASC
            """;
        return jdbc.queryForList(sql);
    }

    // ── Criar agendamento ─────────────────────────────────────────
    public boolean criar(int departamentoId, int usuarioId,
                         String dataAgendamento, String dataFim, String motivo,
                         List<Integer> visitantesIds) {
        String sql = """
            INSERT INTO agendamentos (departamento_id, usuario_id, data_agendamento, data_fim, motivo, status)
            VALUES (?, ?, ?, ?, ?, 'AGENDADO')
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, departamentoId);
            ps.setInt(2, usuarioId);
            ps.setString(3, dataAgendamento);
            ps.setString(4, dataFim);
            ps.setString(5, motivo);
            return ps;
        }, keyHolder);

        if (rows == 0 || keyHolder.getKey() == null) return false;

        int agendamentoId = keyHolder.getKey().intValue();
        for (int visitanteId : visitantesIds) {
            jdbc.update(
                "INSERT INTO agendamentos_visitantes (agendamento_id, visitante_id) VALUES (?, ?)",
                agendamentoId, visitanteId
            );
        }
        return true;
    }

    // ── Confirmar chegada → cria visita ativa ────────────────────
    public boolean confirmarChegada(int agendamentoId, int usuarioId) {
        // Busca dados do agendamento
        String sqlAg = """
            SELECT a.departamento_id, a.motivo,
                   GROUP_CONCAT(av.visitante_id) AS ids_visitantes
            FROM agendamentos a
            LEFT JOIN agendamentos_visitantes av ON av.agendamento_id = a.id
            WHERE a.id = ? AND a.status = 'AGENDADO'
            GROUP BY a.id
            """;
        List<Map<String, Object>> rows = jdbc.queryForList(sqlAg, agendamentoId);
        if (rows.isEmpty()) return false;

        Map<String, Object> ag = rows.get(0);
        int depId   = (int) ag.get("departamento_id");
        String motivo = (String) ag.get("motivo");
        String idsStr = (String) ag.get("ids_visitantes");

        // Cria a visita
        String sqlVisita = "INSERT INTO visitas (departamento_id, usuario_id, data_entrada, motivo) VALUES (?, ?, NOW(), ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sqlVisita, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, depId);
            ps.setInt(2, usuarioId);
            ps.setString(3, motivo);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() == null) return false;
        int visitaId = keyHolder.getKey().intValue();

        // Vincula visitantes na visita
        if (idsStr != null) {
            for (String idStr : idsStr.split(",")) {
                jdbc.update(
                    "INSERT INTO visitantes_visitas (visitantes_id, visitas_id) VALUES (?, ?)",
                    Integer.parseInt(idStr.trim()), visitaId
                );
            }
        }

        // Marca agendamento como CONFIRMADO
        jdbc.update("UPDATE agendamentos SET status = 'CONFIRMADO' WHERE id = ?", agendamentoId);
        return true;
    }

    // ── Cancelar agendamento ──────────────────────────────────────
    public boolean cancelar(int agendamentoId) {
        return jdbc.update(
            "UPDATE agendamentos SET status = 'CANCELADO' WHERE id = ? AND status = 'AGENDADO'",
            agendamentoId
        ) > 0;
    }

    // ── Deletar agendamento ───────────────────────────────────────
    public boolean deletar(int agendamentoId) {
        jdbc.update("DELETE FROM agendamentos_visitantes WHERE agendamento_id = ?", agendamentoId);
        return jdbc.update("DELETE FROM agendamentos WHERE id = ?", agendamentoId) > 0;
    }

    // ── Dados para selects ────────────────────────────────────────
    public List<Map<String, Object>> listarDepartamentos() {
        return jdbc.queryForList("SELECT id, nome FROM departamentos ORDER BY nome");
    }

    public List<Map<String, Object>> listarVisitantes() {
        return jdbc.queryForList("SELECT id, nome, empresa FROM visitantes ORDER BY nome");
    }
}
