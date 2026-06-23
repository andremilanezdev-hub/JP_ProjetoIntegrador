package br.com.controlevisitantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {

    @Autowired
    private JdbcTemplate jdbc;

    // ── Autenticação ─────────────────────────────────────────────
    public boolean autenticar(String login, String senha) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE login = ? AND senha = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, login, senha);
        return count != null && count > 0;
    }

    // ── Listar todos ─────────────────────────────────────────────
    public List<Map<String, Object>> listar() {
        return jdbc.queryForList("SELECT id, nome, login FROM usuarios ORDER BY nome");
    }

    // ── Cadastrar ────────────────────────────────────────────────
    public boolean cadastrar(String nome, String login, String senha) {
        if (existe(login, nome)) return false;
        String sql = "INSERT INTO usuarios (nome, login, senha) VALUES (?, ?, ?)";
        return jdbc.update(sql, nome, login, senha) > 0;
    }

    // ── Deletar ──────────────────────────────────────────────────
    public boolean deletar(int id) {
        return jdbc.update("DELETE FROM usuarios WHERE id = ?", id) > 0;
    }

    // ── Alterar senha ────────────────────────────────────────────
    public boolean alterarSenha(int id, String novaSenha) {
        return jdbc.update("UPDATE usuarios SET senha = ? WHERE id = ?", novaSenha, id) > 0;
    }

    // ── Verificar duplicidade ─────────────────────────────────────
    private boolean existe(String login, String nome) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE login = ? OR nome = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, login, nome);
        return count != null && count > 0;
    }
}
