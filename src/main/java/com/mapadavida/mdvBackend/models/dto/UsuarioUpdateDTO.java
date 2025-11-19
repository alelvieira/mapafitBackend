package com.mapadavida.mdvBackend.models.dto;

import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import lombok.Data;

@Data
public class UsuarioUpdateDTO {
    private String nome;
    private String email;
    private String sexo;
    private String idade;
    private String telefone;
    private Endereco endereco;
    private TipoUsuario tipoUsuario;
    private String senha;
}