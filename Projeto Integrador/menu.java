import java.util.Scanner;

public class menu {

    public static void abrir(String usuarioLogado) {
        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=============================");
            System.out.println("  Controle de Visitantes");
            System.out.println("  Usuário: " + usuarioLogado);
            System.out.println("=============================");
            System.out.println("1 - Cadastrar Visitante");
            System.out.println("2 - Registrar Entrada");
            System.out.println("3 - Registrar Saída");
            System.out.println("4 - Consultar Visitantes");
            System.out.println("5 - Histórico de Visitas");
            System.out.println("0 - Sair");
            System.out.println("=============================");
            System.out.print("Opção: ");

            opcao = sc.nextInt();

            switch (opcao) {
                case 1 -> System.out.println(">> Cadastrar Visitante (em desenvolvimento)");
                case 2 -> System.out.println(">> Registrar Entrada (em desenvolvimento)");
                case 3 -> System.out.println(">> Registrar Saída (em desenvolvimento)");
                case 4 -> System.out.println(">> Consultar Visitantes (em desenvolvimento)");
                case 5 -> System.out.println(">> Histórico de Visitas (em desenvolvimento)");
                case 0 -> System.out.println("Encerrando o sistema. Até logo!");
                default -> System.out.println("Opção inválida.");
            }

        } while (opcao != 0);

        sc.close();
    }
}
