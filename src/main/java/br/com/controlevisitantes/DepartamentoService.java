package br.com.controlevisitantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DepartamentoService {

    @Autowired
    private JdbcTemplate jdbc;

    // ── Listar todos ─────────────────────────────────────────────
    public List<Map<String, Object>> listar() {
        return jdbc.queryForList("SELECT id, nome FROM departamentos ORDER BY nome");
    }

    // ── Cadastrar ────────────────────────────────────────────────
    public boolean cadastrar(String nome) {
        if (nome == null || nome.isBlank()) return false;
        if (existe(nome)) return false;
        return jdbc.update("INSERT INTO departamentos (nome) VALUES (?)", nome.trim()) > 0;
    }

    // ── Deletar ──────────────────────────────────────────────────
    public boolean deletar(int id) {
        return jdbc.update("DELETE FROM departamentos WHERE id = ?", id) > 0;
    }

    // ── Renomear ─────────────────────────────────────────────────
    public boolean renomear(int id, String novoNome) {
        if (novoNome == null || novoNome.isBlank()) return false;
        return jdbc.update("UPDATE departamentos SET nome = ? WHERE id = ?", novoNome.trim(), id) > 0;
    }

    // ── Verificar duplicidade ─────────────────────────────────────
    private boolean existe(String nome) {
        String sql = "SELECT COUNT(*) FROM departamentos WHERE nome LIKE ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, nome.trim());
        return count != null && count > 0;
    }
}
