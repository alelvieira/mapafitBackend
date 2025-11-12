package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.entities.TipoAtividade;
import com.mapadavida.mdvBackend.repositories.TipoAtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoAtividadeService {
    @Autowired
    private TipoAtividadeRepository tipoAtividadeRepository;

    public List<TipoAtividade> findAll() {
        return tipoAtividadeRepository.findAll();
    }

    public Optional<TipoAtividade> findById(long id) {
        return tipoAtividadeRepository.findById(id);
    }

    public void deleteById(Long id) {
        tipoAtividadeRepository.deleteById(id);
    }

    public TipoAtividade save(TipoAtividade tipoAtividade) {
        return tipoAtividadeRepository.save(tipoAtividade);
    }

    public TipoAtividade update(Long id, TipoAtividade dto) {
        return tipoAtividadeRepository.findById(id)
                .map(existing -> {
                    existing.setNome(dto.getNome());
                    return tipoAtividadeRepository.save(existing);
                })
                .orElseGet(() -> {
                    dto.setId(id);
                    return tipoAtividadeRepository.save(dto);
                });
    }

}
