package com.mapadavida.mdvBackend.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mapadavida.mdvBackend.models.dto.UsuarioDTO;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_usuario")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Usuario {
    @Id
    @Column(name = "id_usuario")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_usuario", nullable = false)
    private String nome;

    @Column(name = "email_usuario", nullable = true, unique = true)
    private String email;

    @Column(name = "sexo_usuario")
    private String sexo;

    @Column(name = "idade_usuario", nullable = true)
    private String idade;

    @ManyToOne
    @JoinColumn(name = "id_endereco", referencedColumnName = "id_endereco", nullable = false)
    private com.mapadavida.mdvBackend.models.entities.Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario = TipoUsuario.CADASTRADO;

    @Column(name = "senha_usuario", nullable = false)
    private String senha;

    @OneToMany(mappedBy = "usuario")
    private List<com.mapadavida.mdvBackend.models.entities.Avaliacao> avaliacoes;

    public Usuario(UsuarioDTO usuarioDTO){
        BeanUtils.copyProperties(usuarioDTO, this);
    }

    public Usuario(){}

}


