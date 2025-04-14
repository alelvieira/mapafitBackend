package com.mapadavida.mdvBackend.models.dto;

import com.mapadavida.mdvBackend.models.entities.Usuario;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class LoginDTO {
    private String email;
    private String senha;

    public LoginDTO() {}
    public LoginDTO(Usuario usuario) {
        BeanUtils.copyProperties(usuario, this);
    }


}
