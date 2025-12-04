package com.mapadavida.mdvBackend.config;

import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordMigrationRunner implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2[aby]\\$.{56}\\z");

    public PasswordMigrationRunner(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            for (Usuario u : usuarios) {
                String senha = u.getSenha();
                if (senha == null || senha.isBlank()) continue;

                if (BCRYPT_PATTERN.matcher(senha).matches()) continue;

                if (senha.matches("[0-9a-fA-F]{64}")) {
                    logger.info("Usuário {} possui senha SHA-256; recomendar reset de senha", u.getEmail());
                    continue;
                }

                String encoded = passwordEncoder.encode(senha);
                u.setSenha(encoded);
                usuarioRepository.save(u);
                logger.info("Migrated plain password for user {} to BCrypt", u.getEmail());
            }
        } catch (Exception ex) {
            logger.error("Erro na migração de senhas: {}", ex.getMessage(), ex);
        }
    }
}

