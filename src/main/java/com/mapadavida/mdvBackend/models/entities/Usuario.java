package com.mapadavida.mdvBackend.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mapadavida.mdvBackend.models.dto.UsuarioDTO;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "telefone_usuario")
    private String telefone;

    @Column(name = "sexo_usuario")
    private String sexo;

    @Column(name = "idade_usuario", nullable = true)
    private String idade;

    @ManyToOne
    @JoinColumn(name = "id_endereco", referencedColumnName = "id_endereco", nullable = true)
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario = TipoUsuario.CADASTRADO;

    @Column(name = "senha_usuario", nullable = false)
    private String senha;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "pontos")
    private Integer pontos = 0;

    @OneToMany(mappedBy = "usuario")
    private List<Avaliacao> avaliacoes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_usuario_conquista",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_conquista")
    )
    private Set<Conquista> conquistas = new HashSet<>();
    public Usuario(UsuarioDTO usuarioDTO){
        BeanUtils.copyProperties(usuarioDTO, this);
    }

    public Usuario(){}

}


