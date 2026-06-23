package br.com.controlevisitantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VisitanteService {

    @Autowired
    private JdbcTemplate jdbc;

    // ── Listar todos ─────────────────────────────────────────────
    public List<Map<String, Object>> listar() {
        return jdbc.queryForList(
            "SELECT id, nome, cpf, telefone, empresa FROM visitantes ORDER BY nome"
        );
    }

    // ── Cadastrar ────────────────────────────────────────────────
    public boolean cadastrar(String nome, String cpf, String telefone, String empresa) {
        if (nome.isBlank() || cpf.isBlank() || telefone.isBlank() || empresa.isBlank()) return false;
        if (existeCpf(cpf)) return false;
        // Bug corrigido: vírgulas no INSERT
        String sql = "INSERT INTO visitantes (nome, cpf, telefone, empresa) VALUES (?, ?, ?, ?)";
        return jdbc.update(sql, nome.trim(), cpf.trim(), telefone.trim(), empresa.trim()) > 0;
    }

    // ── Deletar ──────────────────────────────────────────────────
    public boolean deletar(int id) {
        return jdbc.update("DELETE FROM visitantes WHERE id = ?", id) > 0;
    }

    // ── Alterar ──────────────────────────────────────────────────
    public boolean alterar(int id, String nome, String cpf, String telefone, String empresa) {
        if (nome.isBlank() || cpf.isBlank() || telefone.isBlank() || empresa.isBlank()) return false;
        String sql = "UPDATE visitantes SET nome = ?, cpf = ?, telefone = ?, empresa = ? WHERE id = ?";
        return jdbc.update(sql, nome.trim(), cpf.trim(), telefone.trim(), empresa.trim(), id) > 0;
    }

    // ── Verificar CPF duplicado ───────────────────────────────────
    // Bug corrigido: filtrar por cpf, não por nome
    private boolean existeCpf(String cpf) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM visitantes WHERE cpf = ?", Integer.class, cpf.trim()
        );
        return count != null && count > 0;
    }
}
