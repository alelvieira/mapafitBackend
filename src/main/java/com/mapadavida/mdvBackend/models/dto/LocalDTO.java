package com.mapadavida.mdvBackend.models.dto;

import com.mapadavida.mdvBackend.models.entities.Local;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalDTO {

    private Long id;
    private String nome;
    private boolean aprovado;
    private EnderecoDTO endereco;

    private Long tipoAtividadeId;
    private String tipoAtividadeNome;

    private Long tipoAcessoId;
    private String tipoAcessoNome;

    private Long tipoLocalId;
    private String tipoLocalNome;

    private String horariosFuncionamento;
    private String informacoesAdicionais;
    private Integer distancia;

    public LocalDTO() {}

    public LocalDTO(Local local) {
        if (local != null) {
            this.id = local.getId();
            this.nome = local.getNome();
            this.aprovado = local.isAprovado();
            this.horariosFuncionamento = local.getHorariosFuncionamento();
            this.informacoesAdicionais = local.getInformacoesAdicionais();

            if (local.getEndereco() != null) {
                this.endereco = new EnderecoDTO(local.getEndereco());
            }
            if (local.getTipoAtividade() != null) {
                this.tipoAtividadeId = local.getTipoAtividade().getId();
                this.tipoAtividadeNome = local.getTipoAtividade().getNome();
            }
            if (local.getTipoAcesso() != null) {
                this.tipoAcessoId = local.getTipoAcesso().getId();
                this.tipoAcessoNome = local.getTipoAcesso().getNome();
            }
            if (local.getTipoLocal() != null) {
                this.tipoLocalId = local.getTipoLocal().getId();
                this.tipoLocalNome = local.getTipoLocal().getNome();
            }
        }
    }
}
