package br.com.evolucaoparking.config;

import br.com.evolucaoparking.model.PerfilUsuario;
import br.com.evolucaoparking.model.TipoVeiculo;
import br.com.evolucaoparking.model.Usuario;
import br.com.evolucaoparking.model.Vaga;
import br.com.evolucaoparking.repository.UsuarioRepository;
import br.com.evolucaoparking.repository.VagaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(
            VagaRepository vagaRepository,
            UsuarioRepository usuarioRepository,
            ParkingProperties properties,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (vagaRepository.count() == 0) {
                criarVagas(vagaRepository, properties);
            } else {
                garantirVagasMoto(vagaRepository, properties);
            }

            if (usuarioRepository.findByLoginIgnoreCase("admin").isEmpty()) {
                Usuario admin = new Usuario("Administrador", "admin", "", PerfilUsuario.ADMIN);
                admin.setSenha(passwordEncoder.encode("admin123"));
                usuarioRepository.save(admin);
            }

            if (usuarioRepository.findByLoginIgnoreCase("joao").isEmpty()) {
                Usuario func = new Usuario("João Silva", "joao", "", PerfilUsuario.FUNCIONARIO);
                func.setSenha(passwordEncoder.encode("func123"));
                usuarioRepository.save(func);
            }
        };
    }

    private void criarVagas(VagaRepository vagaRepository, ParkingProperties properties) {
        for (int i = 1; i <= properties.getVagasCarro(); i++) {
            vagaRepository.save(new Vaga(i, TipoVeiculo.CARRO));
        }
        garantirVagasMoto(vagaRepository, properties);
    }

    private void garantirVagasMoto(VagaRepository vagaRepository, ParkingProperties properties) {
        for (int i = 1; i <= properties.getVagasMoto(); i++) {
            if (vagaRepository.findByNumeroAndTipoVeiculo(i, TipoVeiculo.MOTO).isEmpty()) {
                vagaRepository.save(new Vaga(i, TipoVeiculo.MOTO));
            }
        }
    }
}
