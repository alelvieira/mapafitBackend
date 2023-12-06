package com.mapadavida.mdvBackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {

   // @Autowired
  //  private JavaMailSender mailSender;
  //  public void emailSenha(String to, String senha){
  //      SimpleMailMessage message = new SimpleMailMessage();
  //      message.setTo(to);
  //      message.setSubject("Sua senha - Mapa da Vida");
  //      message.setText("Olá, sua senha para acessar o Mapa da Vida é: "+senha);
   //     mailSender.send(message);
  //  }
}
