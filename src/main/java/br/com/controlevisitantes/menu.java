package br.com.controlevisitantes;

/**
 * Modelo do Menu Principal.
 * Cada Opcao corresponde a um módulo do sistema.
 * A opção SAIR (5) encerra a sessão e redireciona para o login
 * via Spring Security (/logout → /login).
 */
public class menu {

    public enum Opcao {
        USUARIO(1,      "Usuários",      "/usuarios"),
        VISITANTE(2,    "Visitantes",    "#"),
        DEPARTAMENTO(3, "Departamentos", "#"),
        VISITAS(4,      "Visitas",       "#"),
        SAIR(5,         "Sair",          "/logout");

        private final int    codigo;
        private final String label;
        private final String url;

        Opcao(int codigo, String label, String url) {
            this.codigo = codigo;
            this.label  = label;
            this.url    = url;
        }

        public int    getCodigo() { return codigo; }
        public String getLabel()  { return label;  }
        public String getUrl()    { return url;    }

        /** Retorna true se a opção encerra a sessão e volta ao login. */
        public boolean isSair() { return this == SAIR; }
    }

    /** Retorna todas as opções do menu. */
    public static Opcao[] getOpcoes() {
        return Opcao.values();
    }
}
