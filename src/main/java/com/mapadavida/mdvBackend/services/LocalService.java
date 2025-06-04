package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.dto.EnderecoDTO;
import com.mapadavida.mdvBackend.models.dto.LocalDTO;
import com.mapadavida.mdvBackend.models.entities.*;
import com.mapadavida.mdvBackend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocalService {

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private TipoLocalRepository tipoLocalRepository;

    @Autowired
    private TipoAtividadeRepository tipoAtividadeRepository;

    @Autowired
    private TipoAcessoRepository tipoAcessoRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private EnderecoService enderecoService;


    public List<LocalDTO> getLocais() {
        List<LocalDTO> locais = localRepository.findAll().stream().map(LocalDTO::new).toList();
        return locais;
    }

    public Optional<Local> getLocalById(Long id) {
        return localRepository.findById(id);
    }

    public Optional<Local> findLocal(Local local) {
        return localRepository.findById(local.getId());
    }

    public List<LocalDTO> findLocaisByTipoLocal(Long id) {
        List<Local> locais = localRepository.findByTipoLocalId(id);
        return locais.stream().map(LocalDTO::new).toList();
    }

    public List<LocalDTO> findLocaisByTipoAtividade(Long id) {
        List<Local> locais = localRepository.findByTipoAtividadeId(id);
        return locais.stream().map(LocalDTO::new).toList();
    }

    public List<LocalDTO> findLocaisByTipoAcesso(Long id) {
        List<Local> locais = localRepository.findByTipoAcessoId(id);
        return locais.stream().map(LocalDTO::new).toList();
    }

    public LocalDTO createLocal(LocalDTO localDTO) {
        if (localDTO.getTipoLocalId() != null) {
            TipoLocal tipoLocal = tipoLocalRepository.findById(localDTO.getTipoLocalId())
                    .orElseThrow(() -> new RuntimeException("TipoLocal não encontrado"));
            if (localDTO.getTipoAtividadeId() != null) {
                TipoAtividade tipoAtividade = tipoAtividadeRepository.findById(localDTO.getTipoAtividadeId())
                        .orElseThrow(() -> new RuntimeException("TipoAtividade não encontrado"));
                if (localDTO.getTipoAcessoId() != null) {
                    TipoAcesso tipoAcesso = tipoAcessoRepository.findById(localDTO.getTipoAcessoId())
                            .orElseThrow(() -> new RuntimeException("TipoAcesso não encontrado"));

                    Endereco endereco = enderecoService.salvarEndereco(localDTO.getEndereco(), localDTO.getEndereco().getLatitude().doubleValue(), localDTO.getEndereco().getLongitude().doubleValue());
                    Local local = new Local(localDTO, endereco,  tipoAtividade, tipoAcesso, tipoLocal);

                    localRepository.save(local);

                    return new LocalDTO(local);
                }
            }
        }
        return localDTO;
    }

    public List<LocalDTO> findLocaisProximos(double latitude, double longitude, double raio) {

        List<EnderecoDTO> enderecosDTO = enderecoService.buscarProximos(latitude, longitude, raio);

        return enderecosDTO.stream().map(enderecoDTO -> {
            List<Local> locais = localRepository.findByEnderecoId(enderecoDTO.getId());

             List<LocalDTO> locaisDTO = locais.stream().map(LocalDTO::new).toList();
             locaisDTO.forEach(
                     localDTO -> localDTO.setDistancia(enderecoService.calcularDistancia(latitude, longitude, localDTO.getEndereco()))
             );
             return locaisDTO;
        }).flatMap(List::stream).toList();
    }

    public LocalDTO updateLocal(Long id, LocalDTO dto) {
        Local local = localRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local não encontrado"));

        local.setNome(dto.getNome());
        local.setAprovado(dto.isAprovado());
        local.setHorariosFuncionamento(dto.getHorariosFuncionamento());
        local.setInformacoesAdicionais(dto.getInformacoesAdicionais());

        if (dto.getTipoAtividadeId() != null) {
            local.setTipoAtividade(tipoAtividadeRepository.findById(dto.getTipoAtividadeId())
                    .orElse(null));
        }

        if (dto.getTipoAcessoId() != null) {
            local.setTipoAcesso(tipoAcessoRepository.findById(dto.getTipoAcessoId())
                    .orElse(null));
        }

        if (dto.getTipoLocalId() != null) {
            local.setTipoLocal(tipoLocalRepository.findById(dto.getTipoLocalId())
                    .orElse(null));
        }

        if (dto.getEndereco() != null && dto.getEndereco().getId() != null) {
            Endereco endereco = enderecoRepository.findById(dto.getEndereco().getId())
                    .orElse(null);
            local.setEndereco(endereco);
        }

        Local atualizado = localRepository.save(local);
        return new LocalDTO(atualizado);
    }



}
