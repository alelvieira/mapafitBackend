package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    private static final Logger logger = LoggerFactory.getLogger(TestEmailController.class);

    @Autowired
    private EmailService emailService;

    @GetMapping("/email/test")
    public ResponseEntity<?> sendTestEmail(@RequestParam(required = false) String to) {
        String target = (to == null || to.isBlank()) ? "test@example.com" : to;
        String resetUrl = "https://example.com/reset?token=token123";
        try {
            emailService.sendResetPasswordEmail(target, "Teste", resetUrl);
            return ResponseEntity.ok(java.util.Map.of("message", "enviado para: " + target));
        } catch (Exception ex) {
            logger.error("Erro ao enviar email de teste: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body(java.util.Map.of("error", ex.getMessage()));
        }
    }
}

