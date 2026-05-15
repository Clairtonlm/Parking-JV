package br.com.evolucaoparking.web;

import br.com.evolucaoparking.dto.EntradaRequest;
import br.com.evolucaoparking.model.ModalidadePagamento;
import br.com.evolucaoparking.security.UsuarioDetails;
import br.com.evolucaoparking.service.EstacionamentoService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/operacao")
public class OperacaoController {

    private final EstacionamentoService estacionamentoService;

    public OperacaoController(EstacionamentoService estacionamentoService) {
        this.estacionamentoService = estacionamentoService;
    }

    @ModelAttribute("modalidades")
    public ModalidadePagamento[] modalidades() {
        return ModalidadePagamento.values();
    }

    @GetMapping
    public String painel(Model model) {
        model.addAttribute("operacao", estacionamentoService.carregarOperacao());
        model.addAttribute("entrada", EntradaRequest.vazio());
        return "operacao/index";
    }

    @PostMapping("/entrada")
    public String entrada(
            @Valid @ModelAttribute("entrada") EntradaRequest entrada,
            BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioDetails usuario,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("erro", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/operacao";
        }

        try {
            var registro = estacionamentoService.registrarEntrada(entrada, usuario.getUsuario());
            redirectAttributes.addFlashAttribute("sucesso", "Entrada registrada com sucesso.");
            return "redirect:/recibo/entrada/" + registro.getId();
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/operacao";
        }
    }

    @GetMapping("/preview")
    public String preview(@RequestParam String termo, Model model) {
        try {
            BigDecimal valor = estacionamentoService.previewValorSaida(termo);
            model.addAttribute("previewTermo", termo);
            model.addAttribute("previewValor", valor);
        } catch (IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage());
        }
        model.addAttribute("operacao", estacionamentoService.carregarOperacao());
        model.addAttribute("entrada", EntradaRequest.vazio());
        return "operacao/index";
    }

    @PostMapping("/saida")
    public String saida(
            @RequestParam String termo,
            @AuthenticationPrincipal UsuarioDetails usuario,
            RedirectAttributes redirectAttributes) {

        try {
            var resposta = estacionamentoService.registrarSaida(termo, usuario.getUsuario());
            redirectAttributes.addFlashAttribute("sucesso", "Saída registrada. Valor: R$ " + resposta.valorPago());
            return "redirect:/recibo/saida/" + resposta.registroId();
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/operacao";
        }
    }
}
