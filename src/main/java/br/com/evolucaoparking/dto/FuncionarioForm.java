package br.com.evolucaoparking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FuncionarioForm(
        Long id,
        @NotBlank(message = "Informe o nome")
        @Size(max = 100)
        String nome,
        @NotBlank(message = "Informe o login")
        @Size(max = 80)
        String login,
        @Size(max = 50)
        String senha,
        Boolean ativo
) {
    public static FuncionarioForm vazio() {
        return new FuncionarioForm(null, "", "", "", true);
    }
}
