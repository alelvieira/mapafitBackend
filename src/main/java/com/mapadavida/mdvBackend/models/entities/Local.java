package com.mapadavida.mdvBackend.models.entities;

import com.mapadavida.mdvBackend.models.dto.LocalDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_local")
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "aprovado", nullable = false)
    private boolean aprovado = false;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @ManyToOne
    @JoinColumn(name = "tipo_atividade_id")
    private TipoAtividade tipoAtividade;

    @ManyToOne
    @JoinColumn(name = "tipo_acesso_id")
    private TipoAcesso tipoAcesso;

    @ManyToOne
    @JoinColumn(name = "tipo_local_id")
    private TipoLocal tipoLocal;

    private String horariosFuncionamento;
    private String informacoesAdicionais;

    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avaliacao> avaliacoes;

    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Checkin> checkins;

    public Local() {}

    public Local(LocalDTO localDTO, Endereco endereco, TipoAtividade tipoAtividade, TipoAcesso tipoAcesso, TipoLocal tipoLocal) {
        if (localDTO != null) {
            this.id = localDTO.getId();
            this.nome = localDTO.getNome();
            this.aprovado = localDTO.isAprovado();
            this.horariosFuncionamento = localDTO.getHorariosFuncionamento();
            this.informacoesAdicionais = localDTO.getInformacoesAdicionais();
        }
        this.endereco = endereco;
        this.tipoAtividade = tipoAtividade;
        this.tipoAcesso = tipoAcesso;
        this.tipoLocal = tipoLocal;
    }
}
