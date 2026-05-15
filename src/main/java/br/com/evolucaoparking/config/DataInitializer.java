package br.com.evolucaoparking.config;

import br.com.evolucaoparking.model.PerfilUsuario;
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
                for (int i = 1; i <= properties.getTotalVagas(); i++) {
                    vagaRepository.save(new Vaga(i));
                }
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
}
