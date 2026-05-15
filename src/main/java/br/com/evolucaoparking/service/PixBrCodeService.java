package br.com.evolucaoparking.service;

import br.com.evolucaoparking.config.PixProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;

@Service
public class PixBrCodeService {

    private static final int MAX_NOME = 25;
    private static final int MAX_CIDADE = 15;
    private static final int MAX_TXID = 25;

    private final PixProperties properties;

    public PixBrCodeService(PixProperties properties) {
        this.properties = properties;
    }

    public String gerarPayload(BigDecimal valor, String identificador) {
        String chave = normalizarChave(properties.getChave());
        String nome = sanitizar(properties.getNomeBeneficiario(), MAX_NOME);
        String cidade = sanitizar(properties.getCidade(), MAX_CIDADE);
        String txid = sanitizar(identificador != null ? identificador : "EVOPARK", MAX_TXID);

        boolean comValor = valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
        String valorFormatado = comValor
                ? valor.setScale(2, RoundingMode.HALF_UP).toPlainString()
                : null;

        String merchantAccount = tlv("00", "br.gov.bcb.pix") + tlv("01", chave);
        String additionalData = tlv("05", txid);

        StringBuilder payload = new StringBuilder();
        payload.append(tlv("00", "01"));
        payload.append(tlv("01", comValor ? "12" : "11"));
        payload.append(tlv("26", merchantAccount));
        payload.append(tlv("52", "0000"));
        payload.append(tlv("53", "986"));
        if (comValor) {
            payload.append(tlv("54", valorFormatado));
        }
        payload.append(tlv("58", "BR"));
        payload.append(tlv("59", nome));
        payload.append(tlv("60", cidade));
        payload.append(tlv("62", additionalData));

        return payload + "6304" + crc16(payload.toString());
    }

    private static String tlv(String id, String value) {
        return id + String.format("%02d", value.length()) + value;
    }

    private static String crc16(String payload) {
        int crc = 0xFFFF;
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
                crc &= 0xFFFF;
            }
        }
        return String.format("%04X", crc);
    }

    private static String normalizarChave(String chave) {
        String limpa = chave.trim();
        if (limpa.matches("\\d{10,11}")) {
            String dddNumero = limpa.replaceAll("\\D", "");
            if (dddNumero.length() == 11) {
                return "+55" + dddNumero;
            }
            if (dddNumero.length() == 10) {
                return "+55" + dddNumero;
            }
        }
        if (limpa.matches("\\+?55\\d{10,11}")) {
            return "+55" + limpa.replaceAll("\\D", "").replaceFirst("^55", "");
        }
        return limpa;
    }

    private static String sanitizar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String upper = semAcento.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9 ]", " ").trim();
        if (upper.length() > max) {
            return upper.substring(0, max);
        }
        return upper;
    }
}
