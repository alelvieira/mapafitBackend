package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.dto.EnderecoDTO;
import com.mapadavida.mdvBackend.models.dto.LocalDTO;
import com.mapadavida.mdvBackend.models.entities.*;
import com.mapadavida.mdvBackend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
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
        return getLocais(null, null);
    }

    public List<LocalDTO> getLocais(Double latitude, Double longitude) {
        List<LocalDTO> locais = localRepository.findAll().stream().map(LocalDTO::new).toList();

        if (latitude != null && longitude != null) {
            for (LocalDTO local : locais) {
                if (local.getEndereco() != null) {
                    int distancia = enderecoService.calcularDistancia(
                        latitude,
                        longitude,
                        local.getEndereco()
                    );
                    local.setDistancia(distancia);
                }
            }
        }

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

                    EnderecoDTO enderecoDTO = localDTO.getEndereco();
                    Endereco endereco = new Endereco(enderecoDTO); // Usa o construtor correto
                    endereco = enderecoRepository.save(endereco); // Persiste para evitar o erro de "detached entity"

                    Local local = new Local(localDTO, endereco, tipoAtividade, tipoAcesso, tipoLocal);
                    local = localRepository.save(local);

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
        return localRepository.findById(id).map(local -> {
            local.setNome(dto.getNome());
            local.setAprovado(dto.isAprovado());
            local.setTipoLocal(tipoLocalRepository.findById(dto.getTipoLocalId()).orElse(null));
            local.setTipoAtividade(tipoAtividadeRepository.findById(dto.getTipoAtividadeId()).orElse(null));
            local.setTipoAcesso(tipoAcessoRepository.findById(dto.getTipoAcessoId()).orElse(null));
            local.setHorariosFuncionamento(dto.getHorariosFuncionamento());
            local.setInformacoesAdicionais(dto.getInformacoesAdicionais());

            Endereco endereco = local.getEndereco();
            if (endereco != null) {
                endereco.setRua(dto.getEndereco().getRua());
                endereco.setNumero(dto.getEndereco().getNumero());
                endereco.setCidade(dto.getEndereco().getCidade());
                endereco.setEstado(dto.getEndereco().getEstado());
                endereco.setCep(dto.getEndereco().getCep());

                if (dto.getEndereco().getLatitude() != null && dto.getEndereco().getLongitude() != null) {
                    endereco.setLatitudeLongitude(
                            dto.getEndereco().getLatitude().doubleValue(),
                            dto.getEndereco().getLongitude().doubleValue()
                    );
                }
            }

            localRepository.save(local);
            return new LocalDTO(local);
        }).orElseThrow(() -> new RuntimeException("Local não encontrado"));
    }

    public List<Local> listarAprovados() {
        return localRepository.findByAprovadoTrue();
    }

    public void deletar(Long id) {
        localRepository.findById(id).ifPresent(local -> {
            localRepository.delete(local); // apenas o local, não o endereço
        });
    }


}
