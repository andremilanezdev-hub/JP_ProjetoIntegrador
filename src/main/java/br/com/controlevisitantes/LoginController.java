package br.com.controlevisitantes;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class LoginController {

    @Autowired
    private LoginAttemptService loginAttemptService;

    private static final Random RANDOM = new Random();

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "erro",        required = false) String erro,
            @RequestParam(value = "logout",      required = false) String logout,
            @RequestParam(value = "captchaErro", required = false) String captchaErro,
            HttpSession session,
            Model model) {

        int attempts = loginAttemptService.getAttempts(session);

        if (erro != null) {
            String msg = attempts >= LoginAttemptService.MAX_ATTEMPTS
                    ? "Usuário ou senha inválidos. CAPTCHA obrigatório."
                    : "Usuário ou senha inválidos. Tentativa " + attempts + " de " + LoginAttemptService.MAX_ATTEMPTS + ".";
            model.addAttribute("mensagemErro", msg);
        }

        if (captchaErro != null) {
            model.addAttribute("mensagemErro", "Resposta do CAPTCHA incorreta. Tente novamente.");
        }

        if (logout != null) {
            model.addAttribute("mensagemSucesso", "Você saiu com sucesso.");
        }

        // Gera novo CAPTCHA matemático se necessário
        if (loginAttemptService.isCaptchaRequired(session)) {
            int a = RANDOM.nextInt(9) + 1;
            int b = RANDOM.nextInt(9) + 1;
            session.setAttribute("CAPTCHA_ANSWER", a + b);
            model.addAttribute("captchaRequired", true);
            model.addAttribute("captchaQuestion", a + " + " + b);
        }

        model.addAttribute("tentativas", attempts);

        return "login";
    }
}
