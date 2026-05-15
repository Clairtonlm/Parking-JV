package br.com.evolucaoparking.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String error,
            Model model) {
        if (logout != null) {
            model.addAttribute("msg", "Turno encerrado. Até a próxima!");
        }
        if (error != null) {
            model.addAttribute("erro", "Login ou senha inválidos.");
        }
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
