package br.com.evolucaoparking.service;

import br.com.evolucaoparking.dto.FuncionarioForm;
import br.com.evolucaoparking.model.PerfilUsuario;
import br.com.evolucaoparking.model.Usuario;
import br.com.evolucaoparking.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarFuncionarios() {
        return usuarioRepository.findByPerfilOrderByNomeAsc(PerfilUsuario.FUNCIONARIO);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));
    }

    @Transactional
    public Usuario salvarFuncionario(FuncionarioForm form) {
        Usuario usuario;
        if (form.id() != null) {
            usuario = buscarPorId(form.id());
            if (usuario.getPerfil() != PerfilUsuario.FUNCIONARIO) {
                throw new IllegalArgumentException("Usuário não é funcionário.");
            }
        } else {
            usuario = new Usuario();
            usuario.setPerfil(PerfilUsuario.FUNCIONARIO);
            usuario.setAtivo(true);
        }

        boolean loginEmUso = form.id() == null
                ? usuarioRepository.findByLoginIgnoreCase(form.login()).isPresent()
                : usuarioRepository.existsByLoginIgnoreCaseAndIdNot(form.login(), form.id());
        if (loginEmUso) {
            throw new IllegalStateException("Login já cadastrado: " + form.login());
        }

        usuario.setNome(form.nome().trim());
        usuario.setLogin(form.login().trim().toLowerCase());

        if (form.senha() != null && !form.senha().isBlank()) {
            if (form.senha().length() < 4) {
                throw new IllegalStateException("Senha deve ter no mínimo 4 caracteres.");
            }
            usuario.setSenha(passwordEncoder.encode(form.senha()));
        } else if (usuario.getId() == null) {
            throw new IllegalStateException("Informe uma senha para o novo funcionário.");
        }

        if (form.ativo() != null) {
            usuario.setAtivo(form.ativo());
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarStatus(Long id, boolean ativo) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getPerfil() != PerfilUsuario.FUNCIONARIO) {
            throw new IllegalArgumentException("Apenas funcionários podem ser alterados aqui.");
        }
        usuario.setAtivo(ativo);
        usuarioRepository.save(usuario);
    }
}
