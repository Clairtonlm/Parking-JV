package br.com.evolucaoparking.web;

import br.com.evolucaoparking.dto.FuncionarioForm;
import br.com.evolucaoparking.dto.TurnoResumoView;
import br.com.evolucaoparking.service.TurnoService;
import br.com.evolucaoparking.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final TurnoService turnoService;

    public AdminController(UsuarioService usuarioService, TurnoService turnoService) {
        this.usuarioService = usuarioService;
        this.turnoService = turnoService;
    }

    @GetMapping
    public String painel(Model model) {
        model.addAttribute("turnos", resumirTurnos());
        return "admin/index";
    }

    @GetMapping("/funcionarios")
    public String funcionarios(Model model) {
        model.addAttribute("funcionarios", usuarioService.listarFuncionarios());
        return "admin/funcionarios";
    }

    @GetMapping("/funcionarios/novo")
    public String novoFuncionario(Model model) {
        model.addAttribute("form", FuncionarioForm.vazio());
        return "admin/funcionario-form";
    }

    @GetMapping("/funcionarios/{id}/editar")
    public String editarFuncionario(@PathVariable Long id, Model model) {
        var u = usuarioService.buscarPorId(id);
        model.addAttribute("form", new FuncionarioForm(
                u.getId(), u.getNome(), u.getLogin(), "", u.isAtivo()));
        return "admin/funcionario-form";
    }

    @PostMapping("/funcionarios/salvar")
    public String salvarFuncionario(
            @Valid @ModelAttribute("form") FuncionarioForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/funcionario-form";
        }

        try {
            usuarioService.salvarFuncionario(form);
            redirectAttributes.addFlashAttribute("sucesso", "Funcionário salvo com sucesso.");
            return "redirect:/admin/funcionarios";
        } catch (IllegalStateException | IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            return "admin/funcionario-form";
        }
    }

    @PostMapping("/funcionarios/{id}/status")
    public String alterarStatus(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam boolean ativo,
            RedirectAttributes redirectAttributes) {
        usuarioService.alterarStatus(id, ativo);
        redirectAttributes.addFlashAttribute("sucesso", ativo ? "Funcionário ativado." : "Funcionário desativado.");
        return "redirect:/admin/funcionarios";
    }

    private List<TurnoResumoView> resumirTurnos() {
        return turnoService.listarTodos().stream()
                .map(t -> TurnoResumoView.from(
                        t,
                        turnoService.contarCarrosNoTurno(t),
                        turnoService.contarMotosNoTurno(t),
                        turnoService.caixaDoTurno(t)))
                .toList();
    }
}
