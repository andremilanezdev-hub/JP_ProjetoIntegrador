import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class usuario {

    public static void UserM(String usu) throws SQLException {
        Scanner sc = new Scanner(System.in);

        String usuario="", nome="",login="",senha="";
        int opcao = 0, idUser=0;

           do {
               System.out.printf("""
                       ====== Usuário ====
                       1 - Listar
                       2 - Cadastrar
                       3 - Deletar
                       4 - Alterar
                       5 - Voltar Menu Anterior
                       """);
               opcao = sc.nextInt();

               switch (opcao){
                   case 1:
                       listar();
                       break;
                   case 2:
                       sc.nextLine();
                       System.out.print("Informe o Nome: ");
                       nome = sc.nextLine();
                       System.out.print("Informe um Login: ");
                       login = sc.nextLine();
                       System.out.print("Informe uma Senha: ");
                       senha = sc.nextLine();

                   if (nome.equals(" ") || usuario.equals(" ") || senha.equals(" ")){
                       System.out.println("Nenhuma informação deve estar vaiza");
                   }else if (verifica(login,nome)) {
                       System.out.print("Usário ou Login já cadastrado");
                   }else{
                       boolean ok = conexao_banco.inserir("INSERT INTO usuarios (nome, login, senha) VALUES (?, ?, ?)", nome, login, senha);
                       if (ok) {
                           System.out.println("Usuário cadastrado com sucesso!");
                       } else {
                           System.out.println("Erro ao cadastrar.");
                       }
                   }
                       break;
                   case 3:
                       listar();
                       System.out.print("Informe o ID do Usuário que deseja excluir: ");
                       idUser = sc.nextInt();

                        boolean del = conexao_banco.deletar("DELETE FROM usuarios WHERE id = ?",idUser);
                       if (del) {
                           System.out.println("Usuário deletado com sucesso!");
                       } else {
                           System.out.println(del);
                       }
                       break;
                   case 4:
                       listar();
                       System.out.print("Informe o ID do Usuário que deseja alterar: ");
                       idUser = sc.nextInt();

                       boolean del2 = conexao_banco.deletar("DELETE FROM usuarios WHERE id = ?",idUser);
                       if (del2) {
                           System.out.println("Usuário deletado com sucesso!");
                       } else {
                           System.out.println(del2);
                       }

                       break;
                   case 5:
                       menu.abrir(usu);
                       break;
                   default:
                       System.out.println("Opção Inválida");
                       break;
               }

           }while (opcao !=5);
    }

    private static void listar() throws SQLException {
        ResultSet rs = conexao_banco.getSelect("SELECT * FROM usuarios");
        System.out.println("Id   -  Nome  -  Usuário ");
        while (rs.next()) {
            System.out.printf(" %d - %s - %s \n", rs.getInt("id"), rs.getString("nome"), rs.getString("login"));
        }
    }

    private static boolean verifica(String login, String nome) {
        String sql = "SELECT id FROM usuarios WHERE login = ? AND senha = ?";
        return conexao_banco.existe(sql, login, nome);
    }

}
