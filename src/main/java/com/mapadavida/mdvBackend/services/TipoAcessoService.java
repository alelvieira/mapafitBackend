package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.entities.TipoAcesso;
import com.mapadavida.mdvBackend.repositories.TipoAcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoAcessoService {
    @Autowired
    private TipoAcessoRepository tipoAcessoRepository;

    public List<TipoAcesso> findAll() {
        return tipoAcessoRepository.findAll();
    }

    public TipoAcesso save(TipoAcesso tipoAcesso) {
        return tipoAcessoRepository.save(tipoAcesso);
    }

    public void delete(TipoAcesso tipoAcesso) {
        tipoAcessoRepository.delete(tipoAcesso);
    }

    public Optional<TipoAcesso> findById(Long id) {
        return tipoAcessoRepository.findById(id);
    }
}
