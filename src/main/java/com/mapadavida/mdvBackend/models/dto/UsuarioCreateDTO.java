package com.mapadavida.mdvBackend.models.dto;

import lombok.Data;

@Data
public class UsuarioCreateDTO {
    private String nome;
    private String email;
    private String senha;
    private String sexo;
    private Integer idade;
    private EnderecoDTO endereco; // Pode ser nulo
}