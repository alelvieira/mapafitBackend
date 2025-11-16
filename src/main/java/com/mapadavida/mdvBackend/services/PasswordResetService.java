package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.entities.PasswordResetToken;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.repositories.PasswordResetTokenRepository;
import com.mapadavida.mdvBackend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.passwordReset.tokenExpirationMinutes:60}")
    private long tokenExpirationMinutes;

    // gera token legível (hex) e retorna par (tokenPlain, tokenHash)
    public TokenPair generateTokenForUser(Usuario usuario) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);
        String hash = sha256Hex(token);
        Instant expiresAt = Instant.now().plus(Duration.ofMinutes(tokenExpirationMinutes));

        PasswordResetToken prt = new PasswordResetToken(usuario, hash, expiresAt);
        tokenRepository.save(prt);

        return new TokenPair(token, hash);
    }

    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return tokenRepository.findByTokenHash(tokenHash);
    }

    // marca token como usado
    public void markTokenUsed(PasswordResetToken token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }

    // expose hashing helper to be able to compute hex hash for comparisons
    public String hashToken(String token) {
        return sha256Hex(token);
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static class TokenPair {
        public final String token;
        public final String hash;

        public TokenPair(String token, String hash) {
            this.token = token;
            this.hash = hash;
        }
    }
}
