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
public class VisitaService {

    @Autowired
    private JdbcTemplate jdbc;

    // ── Listar todas as visitas com JOIN ──────────────────────────
    public List<Map<String, Object>> listar() {
        String sql = """
            SELECT
                v.id,
                v.data_entrada,
                v.data_saida,
                v.motivo,
                d.nome   AS departamento,
                u.nome   AS usuario,
                GROUP_CONCAT(vis.nome SEPARATOR ', ') AS visitantes
            FROM visitas v
            LEFT JOIN departamentos d  ON d.id = v.departamento_id
            LEFT JOIN usuarios u       ON u.id = v.usuario_id
            LEFT JOIN visitantes_visitas vv ON vv.visitas_id = v.id
            LEFT JOIN visitantes vis   ON vis.id = vv.visitantes_id
            GROUP BY v.id
            ORDER BY v.data_entrada DESC
            """;
        return jdbc.queryForList(sql);
    }

    // ── Visitas em aberto (sem data_saida) ────────────────────────
    public List<Map<String, Object>> listarEmAberto() {
        String sql = """
            SELECT
                v.id,
                v.data_entrada,
                v.motivo,
                d.nome AS departamento,
                GROUP_CONCAT(vis.nome SEPARATOR ', ') AS visitantes
            FROM visitas v
            LEFT JOIN departamentos d  ON d.id = v.departamento_id
            LEFT JOIN visitantes_visitas vv ON vv.visitas_id = v.id
            LEFT JOIN visitantes vis   ON vis.id = vv.visitantes_id
            WHERE v.data_saida IS NULL
            GROUP BY v.id
            ORDER BY v.data_entrada DESC
            """;
        return jdbc.queryForList(sql);
    }

    // ── Registrar entrada ─────────────────────────────────────────
    public boolean registrarEntrada(int departamentoId, int usuarioId,
                                    String motivo, List<Integer> visitantesIds) {
        String sql = "INSERT INTO visitas (departamento_id, usuario_id, data_entrada, motivo) VALUES (?, ?, NOW(), ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, departamentoId);
            ps.setInt(2, usuarioId);
            ps.setString(3, motivo);
            return ps;
        }, keyHolder);

        if (rows == 0 || keyHolder.getKey() == null) return false;

        int visitaId = keyHolder.getKey().intValue();

        // Vincular visitantes — rejeita se algum já tiver visita em andamento
        for (int visitanteId : visitantesIds) {
            Integer emVisita = jdbc.queryForObject(
                """
                SELECT COUNT(*) FROM visitantes_visitas vv
                JOIN visitas vi ON vi.id = vv.visitas_id
                WHERE vv.visitantes_id = ? AND vi.data_saida IS NULL
                """,
                Integer.class, visitanteId
            );
            if (emVisita != null && emVisita > 0) {
                // Desfaz a visita recém-criada e sinaliza erro
                jdbc.update("DELETE FROM visitas WHERE id = ?", visitaId);
                return false;
            }
            jdbc.update(
                "INSERT INTO visitantes_visitas (visitantes_id, visitas_id) VALUES (?, ?)",
                visitanteId, visitaId
            );
        }
        return true;
    }

    // ── Registrar saída ───────────────────────────────────────────
    public boolean registrarSaida(int visitaId) {
        return jdbc.update(
            "UPDATE visitas SET data_saida = NOW() WHERE id = ? AND data_saida IS NULL",
            visitaId
        ) > 0;
    }

    // ── Deletar visita ────────────────────────────────────────────
    public boolean deletar(int visitaId) {
        jdbc.update("DELETE FROM visitantes_visitas WHERE visitas_id = ?", visitaId);
        return jdbc.update("DELETE FROM visitas WHERE id = ?", visitaId) > 0;
    }

    // ── Dados para os selects do formulário ──────────────────────
    public List<Map<String, Object>> listarDepartamentos() {
        return jdbc.queryForList("SELECT id, nome FROM departamentos ORDER BY nome");
    }

    public List<Map<String, Object>> listarUsuarios() {
        return jdbc.queryForList("SELECT id, nome FROM usuarios ORDER BY nome");
    }

    public List<Map<String, Object>> listarVisitantes() {
        // Inclui flag em_visita = 1 se o visitante já tem visita em andamento
        String sql = """
            SELECT
                v.id,
                v.nome,
                v.empresa,
                CASE WHEN EXISTS (
                    SELECT 1 FROM visitantes_visitas vv
                    JOIN visitas vi ON vi.id = vv.visitas_id
                    WHERE vv.visitantes_id = v.id AND vi.data_saida IS NULL
                ) THEN 1 ELSE 0 END AS em_visita
            FROM visitantes v
            ORDER BY v.nome
            """;
        return jdbc.queryForList(sql);
    }
}
