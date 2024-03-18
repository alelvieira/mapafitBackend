package com.mapadavida.mdvBackend.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mapadavida.mdvBackend.models.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


import java.sql.Date;

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

    @Column(name = "sexo_usuario", unique = true)
    private String sexo;

    @Column(name = "token_usuario", nullable = false, unique = true)
    private String token;

    @Column(name = "idade_usuario", nullable = true)
    private String idade;

    @ManyToOne
    @JoinColumn(name = "id_endereco", referencedColumnName = "id_endereco", nullable = false)
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario = TipoUsuario.CADASTRADO;

    @Column(name = "senha_usuario", nullable = false)
    private String senha;

    @OneToMany(mappedBy = "usuario")
    private List<Avaliacao> avaliacoes;
}

