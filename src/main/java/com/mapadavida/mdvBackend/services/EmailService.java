package com.mapadavida.mdvBackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String name, String resetUrl) {
        if (mailSender == null) {
            // JavaMailSender não está disponível — provavelmente dependência/configuração ausente
            System.err.println("JavaMailSender não configurado; e-mail não enviado: " + to);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Redefinição de senha - Mapa da Vida");

            String html = "<p>Olá " + (name != null ? name : "") + ",</p>"
                    + "<p>Recebemos uma solicitação para redefinir sua senha. Clique no link abaixo para criar uma nova senha (válido por algumas horas):</p>"
                    + "<p><a href=\"" + resetUrl + "\">Redefinir senha</a></p>"
                    + "<p>Se você não solicitou essa alteração, ignore este e-mail.</p>";

            String text = "Olá " + (name != null ? name : "") + ",\n\n"
                    + "Recebemos uma solicitação para redefinir sua senha. Use o link abaixo:\n"
                    + resetUrl + "\n\n"
                    + "Se você não solicitou essa alteração, ignore este e-mail.";

            helper.setText(text, html);
            helper.setFrom("no-reply@mapadavida.com");
            mailSender.send(message);
        } catch (Exception ex) {
            // log e siga em frente (não propague para usuário final)
            System.err.println("Falha ao enviar email de redefinição: " + ex.getMessage());
        }
    }
}
