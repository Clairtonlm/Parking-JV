package br.com.evolucaoparking.web;

import br.com.evolucaoparking.service.EstacionamentoService;
import br.com.evolucaoparking.service.ReciboPixService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;

@Controller
@RequestMapping("/recibo")
public class ReciboController {

    private final EstacionamentoService estacionamentoService;
    private final ReciboPixService reciboPixService;

    public ReciboController(
            EstacionamentoService estacionamentoService,
            ReciboPixService reciboPixService) {
        this.estacionamentoService = estacionamentoService;
        this.reciboPixService = reciboPixService;
    }

    @GetMapping("/entrada/{id}")
    public String reciboEntrada(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean reimprimir,
            Model model) {
        var registro = estacionamentoService.buscarRegistro(id);
        model.addAttribute("registro", registro);
        model.addAttribute("rotuloVaga", EstacionamentoService.rotuloVaga(registro));
        model.addAttribute("tipo", "ENTRADA");
        model.addAttribute("reimpressao", reimprimir);
        reciboPixService.adicionarAoRecibo(model, registro, "ENTRADA");
        return "recibo/recibo";
    }

    @GetMapping("/saida/{id}")
    public String reciboSaida(@PathVariable Long id, Model model) {
        var registro = estacionamentoService.buscarRegistro(id);
        long minutos = Duration.between(registro.getEntrada(), registro.getSaida()).toMinutes();
        model.addAttribute("registro", registro);
        model.addAttribute("rotuloVaga", EstacionamentoService.rotuloVaga(registro));
        model.addAttribute("minutos", minutos);
        model.addAttribute("tipo", "SAIDA");
        model.addAttribute("reimpressao", false);
        reciboPixService.adicionarAoRecibo(model, registro, "SAIDA");
        return "recibo/recibo";
    }
}
