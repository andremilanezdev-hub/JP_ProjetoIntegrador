import java.sql.*;
import java.util.Scanner;

public class login {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Usuário: ");
        String usuario = sc.nextLine();

        System.out.print("Senha: ");
        String senha = sc.nextLine();

        sc.close();

        if (autenticar(usuario, senha)) {
            menu.abrir(usuario);
        } else {
            System.out.println("Usuário ou senha inválidos.");
        }
    }

    /**
     * Valida login e senha consultando a tabela usuarios.
     * Usa PreparedStatement para evitar SQL Injection.
     */
    private static boolean autenticar(String login, String senha) {
        String sql = "SELECT id FROM usuarios WHERE login = ? AND senha = ?";
        return conexao_banco.existe(sql, login, senha);
    }
}
