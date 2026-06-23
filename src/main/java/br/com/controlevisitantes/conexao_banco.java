package br.com.controlevisitantes;

import java.sql.*;

public class conexao_banco {

    // Static para poder usar em getConexao() sem instanciar a classe
    private static final String URL      = "jdbc:mysql://localhost:3306/controle_visitantes";
    private static final String USER     = "ajava";
    private static final String PASSWORD = "SenhaForte123@";

    public static Connection getConexao() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado. Verifique se o JAR está no classpath.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static ResultSet getSelect(String sql, Object... params) throws SQLException {
        Connection conexao = getConexao();
        PreparedStatement stmt = conexao.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }


    public static boolean inserir(String sql, Object... params) {
        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir no banco: " + e.getMessage());
            return false;
        }
    }

    public static boolean deletar(String sql, Object... params) {
        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao deletar no banco: " + e.getMessage());
            return false;
        }
    }


    public static boolean altera(String sql, Object... params) {
        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar no banco: " + e.getMessage());
            return false;
        }
    }

    public static boolean existe(String sql, Object... params) {
        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar o banco: " + e.getMessage());
            return false;
        }
    }

}
