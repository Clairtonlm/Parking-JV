package br.com.evolucaoparking.web;

import br.com.evolucaoparking.service.EstacionamentoService;
import br.com.evolucaoparking.service.TarifaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/recibo")
public class ReciboController {

    private final EstacionamentoService estacionamentoService;
    private final TarifaService tarifaService;

    public ReciboController(EstacionamentoService estacionamentoService, TarifaService tarifaService) {
        this.estacionamentoService = estacionamentoService;
        this.tarifaService = tarifaService;
    }

    @GetMapping("/entrada/{id}")
    public String reciboEntrada(@PathVariable Long id, Model model) {
        var registro = estacionamentoService.buscarRegistro(id);
        model.addAttribute("registro", registro);
        model.addAttribute("tipo", "ENTRADA");
        return "recibo/recibo";
    }

    @GetMapping("/saida/{id}")
    public String reciboSaida(@PathVariable Long id, Model model) {
        var registro = estacionamentoService.buscarRegistro(id);
        long minutos = Duration.between(registro.getEntrada(), registro.getSaida()).toMinutes();
        model.addAttribute("registro", registro);
        model.addAttribute("minutos", minutos);
        model.addAttribute("tipo", "SAIDA");
        return "recibo/recibo";
    }
}
