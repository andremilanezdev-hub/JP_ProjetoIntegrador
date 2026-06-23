package br.com.controlevisitantes;

import java.sql.*;

/**
 * Classe de autenticação — versão legada (console).
 * No contexto web, a autenticação é feita pelo CustomUserDetailsService + Spring Security.
 */
public class login {

    public static boolean autenticar(String loginUsuario, String senha) {
        String sql = "SELECT id FROM usuarios WHERE login = ? AND senha = ?";
        try (Connection conexao = conexao_banco.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, loginUsuario);
            stmt.setString(2, senha);
            try (ResultSet resposta = stmt.executeQuery()) {
                return resposta.next();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar o banco: " + e.getMessage());
            return false;
        }
    }
}
