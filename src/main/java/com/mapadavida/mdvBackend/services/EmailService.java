package com.mapadavida.mdvBackend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.UUID;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    // use spring.mail.username como remetente se configurado, senão fallback
    @Value("${spring.mail.username:no-reply@mapadavida.com}")
    private String defaultFrom;

    public void sendResetPasswordEmail(String to, String name, String resetUrl) {
        // Monta texto e html do e-mail
        String html = "<p>Olá " + (name != null ? name : "") + ",</p>"
                + "<p>Recebemos uma solicitação para redefinir sua senha. Clique no link abaixo para criar uma nova senha (válido por algumas horas):</p>"
                + "<p><a href=\"" + resetUrl + "\">Redefinir senha</a></p>"
                + "<p>Se você não solicitou essa alteração, ignore este e-mail.</p>";

        String text = "Olá " + (name != null ? name : "") + ",\n\n"
                + "Recebemos uma solicitação para redefinir sua senha. Use o link abaixo:\n"
                + resetUrl + "\n\n"
                + "Se você não solicitou essa alteração, ignore este e-mail.";

        // Se mailSender está configurado, tenta enviar por SMTP
        if (mailSender != null) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject("Redefinição de senha - Mapa da Vida");

                helper.setText(text, html);
                helper.setFrom(defaultFrom);
                mailSender.send(message);
                logger.info("Email de redefinição enviado para {}", to);
                return;
            } catch (Exception ex) {
                // Log e segue para fallback local
                logger.error("Falha ao enviar email via SMTP para {}: {}", to, ex.getMessage());
            }
        } else {
            logger.warn("JavaMailSender não configurado; usando fallback local para e-mail: {}", to);
        }

        // Fallback prático: escrever o conteúdo do e-mail em arquivo local para desenvolvimento
        try {
            Path outDir = Path.of("mail-output");
            Files.createDirectories(outDir);
            // Use epoch millis to avoid characters invalid on Windows filenames
            String fileName = "reset-email-" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID() + ".txt";
            Path outFile = outDir.resolve(fileName);
            String content = "To: " + to + "\n"
                    + "From: " + defaultFrom + "\n"
                    + "Subject: Redefinição de senha - Mapa da Vida\n\n"
                    + text + "\n\n--- HTML ---\n"
                    + html + "\n";

            Files.writeString(outFile, content, StandardOpenOption.CREATE_NEW);
            logger.info("E-mail gravado localmente em {} (abra para ver o link de redefinição)", outFile.toAbsolutePath());
        } catch (Exception ex) {
            logger.error("Falha ao gravar e-mail localmente para {}: {}", to, ex.getMessage(), ex);
        }
    }
}
