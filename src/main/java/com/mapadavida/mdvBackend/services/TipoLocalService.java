package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.entities.TipoLocal;
import com.mapadavida.mdvBackend.repositories.TipoLocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoLocalService {
    @Autowired
    private TipoLocalRepository tipoLocalRepository;

    public void deleteById(Long id) {
        tipoLocalRepository.deleteById(id);
    }

    // Adicionado: métodos para listar, buscar por id, salvar e atualizar TipoLocal
    public List<TipoLocal> findAll() {
        return tipoLocalRepository.findAll();
    }

    public Optional<TipoLocal> findById(Long id) {
        return tipoLocalRepository.findById(id);
    }

    public TipoLocal save(TipoLocal tipoLocal) {
        return tipoLocalRepository.save(tipoLocal);
    }

    public TipoLocal update(Long id, TipoLocal dto) {
        return tipoLocalRepository.findById(id)
                .map(existing -> {
                    existing.setNome(dto.getNome());
                    return tipoLocalRepository.save(existing);
                })
                .orElseGet(() -> {
                    // se não existe, cria novo com o id solicitado (seguindo comportamento simples)
                    dto.setId(id);
                    return tipoLocalRepository.save(dto);
                });
    }
}
