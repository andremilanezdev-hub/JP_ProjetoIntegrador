package br.com.controlevisitantes;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/menu")
    public String menu(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("usuarioLogado", userDetails.getUsername());
        return "menu"; // → templates/menu.html
    }
}
