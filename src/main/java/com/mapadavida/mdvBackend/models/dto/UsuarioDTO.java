package com.mapadavida.mdvBackend.models.dto;

import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String sexo;
    private String idade;
    private Endereco endereco;
    private TipoUsuario tipoUsuario;

    public UsuarioDTO(Usuario usuario) {
        BeanUtils.copyProperties(usuario, this);
    }
}
