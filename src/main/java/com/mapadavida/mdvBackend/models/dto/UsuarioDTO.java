package com.mapadavida.mdvBackend.models.dto;

import com.mapadavida.mdvBackend.models.entities.Conquista;
import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String sexo;
    private String idade;
    private String telefone;
    private EnderecoDTO endereco;
    private TipoUsuario tipoUsuario;
    private String fotoUrl;
    private Integer pontos;
    private Set<Conquista> conquistas;

    public UsuarioDTO(Usuario usuario) {
        BeanUtils.copyProperties(usuario, this);
        if (usuario.getEndereco() != null) {
            this.endereco = new EnderecoDTO(usuario.getEndereco());
        }
    }

}


