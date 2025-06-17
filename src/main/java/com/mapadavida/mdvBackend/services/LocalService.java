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
        // Validar e obter entidades relacionadas
        TipoLocal tipoLocal = tipoLocalRepository.findById(localDTO.getTipoLocalId())
                .orElseThrow(() -> new RuntimeException("TipoLocal não encontrado"));

        TipoAtividade tipoAtividade = tipoAtividadeRepository.findById(localDTO.getTipoAtividadeId())
                .orElseThrow(() -> new RuntimeException("TipoAtividade não encontrado"));

        TipoAcesso tipoAcesso = tipoAcessoRepository.findById(localDTO.getTipoAcessoId())
                .orElseThrow(() -> new RuntimeException("TipoAcesso não encontrado"));

        // Criar e salvar endereço (como entidade nova)
        Endereco endereco = new Endereco(localDTO.getEndereco());
        endereco.setId(null); // Garante que o JPA trate como novo
        endereco.setLatitudeLongitude(
                localDTO.getEndereco().getLatitude().doubleValue(),
                localDTO.getEndereco().getLongitude().doubleValue()
        );
        endereco = enderecoRepository.save(endereco);

        // Criar Local
        Local local = new Local(localDTO, endereco, tipoAtividade, tipoAcesso, tipoLocal);

        // Persistir Local com endereço já salvo
        localRepository.save(local);

        return new LocalDTO(local);
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
        if (!localRepository.existsById(id)) {
            throw new EntityNotFoundException("Local não encontrado");
        }
        localRepository.deleteById(id);
    }

}
