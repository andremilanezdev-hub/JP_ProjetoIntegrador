package br.com.controlevisitantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Integra a tabela `usuarios` do MySQL com o Spring Security.
 * Nota: senhas estão em texto plano no banco (compatível com o sistema atual).
 * Para produção, migrar para BCrypt.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        String sql = "SELECT login, senha FROM usuarios WHERE login = ?";
        try {
            Map<String, Object> row = jdbc.queryForMap(sql, login);
            String senha = (String) row.get("senha");
            return User.withUsername(login)
                    .password("{noop}" + senha)   // {noop} = sem hash (texto plano)
                    .roles("USER")
                    .build();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + login);
        }
    }
}
