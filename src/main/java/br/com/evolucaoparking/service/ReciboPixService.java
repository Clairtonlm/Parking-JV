package br.com.evolucaoparking.service;

import br.com.evolucaoparking.config.PixProperties;
import br.com.evolucaoparking.model.RegistroEstacionamento;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;

@Service
public class ReciboPixService {

    private final PixProperties pixProperties;
    private final PixBrCodeService pixBrCodeService;
    private final QrCodeImageService qrCodeImageService;

    public ReciboPixService(
            PixProperties pixProperties,
            PixBrCodeService pixBrCodeService,
            QrCodeImageService qrCodeImageService) {
        this.pixProperties = pixProperties;
        this.pixBrCodeService = pixBrCodeService;
        this.qrCodeImageService = qrCodeImageService;
    }

    public void adicionarAoRecibo(Model model, RegistroEstacionamento registro, String tipo) {
        if (!pixProperties.isEnabled()) {
            model.addAttribute("pixAtivo", false);
            return;
        }

        BigDecimal valor = null;
        if ("SAIDA".equals(tipo)) {
            valor = registro.getValorPago();
        }

        String payload = pixBrCodeService.gerarPayload(valor, registro.getCodigoRecibo());
        String qrBase64;
        try {
            qrBase64 = qrCodeImageService.gerarBase64Png(payload, 220);
        } catch (NoClassDefFoundError | Exception ex) {
            model.addAttribute("pixAtivo", true);
            model.addAttribute("pixPayload", payload);
            model.addAttribute("pixChave", pixProperties.getChave());
            model.addAttribute("pixComValor", valor != null && valor.compareTo(BigDecimal.ZERO) > 0);
            model.addAttribute("pixValor", valor);
            model.addAttribute("pixErro", "Reinicie o servidor (mvn clean spring-boot:run) para carregar o QR Code.");
            return;
        }

        model.addAttribute("pixAtivo", true);
        model.addAttribute("pixPayload", payload);
        model.addAttribute("pixQrCodeBase64", qrBase64);
        model.addAttribute("pixChave", pixProperties.getChave());
        model.addAttribute("pixComValor", valor != null && valor.compareTo(BigDecimal.ZERO) > 0);
        model.addAttribute("pixValor", valor);
    }
}
