package br.com.controlevisitantes;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepta POST /login quando o CAPTCHA é obrigatório.
 * Roda ANTES do UsernamePasswordAuthenticationFilter do Spring Security.
 */
@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        if ("POST".equalsIgnoreCase(request.getMethod())
                && "/login".equals(request.getServletPath())) {

            HttpSession session = request.getSession(false);

            if (session != null && loginAttemptService.isCaptchaRequired(session)) {
                String userAnswer   = request.getParameter("captchaAnswer");
                Integer correctAnswer = (Integer) session.getAttribute("CAPTCHA_ANSWER");

                boolean correto = correctAnswer != null
                        && userAnswer != null
                        && String.valueOf(correctAnswer).equals(userAnswer.trim());

                if (!correto) {
                    response.sendRedirect(request.getContextPath() + "/login?captchaErro=true");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }
}
