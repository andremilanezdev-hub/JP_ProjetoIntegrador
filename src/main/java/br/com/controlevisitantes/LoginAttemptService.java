package br.com.controlevisitantes;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 3;

    private static final String KEY_ATTEMPTS = "LOGIN_ATTEMPTS";
    private static final String KEY_CAPTCHA   = "CAPTCHA_REQUIRED";
    private static final String KEY_ANSWER    = "CAPTCHA_ANSWER";

    // ── Registra falha ────────────────────────────────────────────
    public void loginFailed(HttpSession session) {
        int attempts = getAttempts(session) + 1;
        session.setAttribute(KEY_ATTEMPTS, attempts);
        if (attempts >= MAX_ATTEMPTS) {
            session.setAttribute(KEY_CAPTCHA, true);
        }
    }

    // ── Registra sucesso — limpa tudo ─────────────────────────────
    public void loginSucceeded(HttpSession session) {
        session.removeAttribute(KEY_ATTEMPTS);
        session.removeAttribute(KEY_CAPTCHA);
        session.removeAttribute(KEY_ANSWER);
    }

    // ── Consultas ─────────────────────────────────────────────────
    public boolean isCaptchaRequired(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(KEY_CAPTCHA));
    }

    public int getAttempts(HttpSession session) {
        Integer v = (Integer) session.getAttribute(KEY_ATTEMPTS);
        return v != null ? v : 0;
    }
}
