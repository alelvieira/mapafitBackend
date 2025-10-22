package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.entities.TipoLocal;
import com.mapadavida.mdvBackend.repositories.TipoLocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoLocalService {
    @Autowired
    private TipoLocalRepository tipoLocalRepository;

    public void deleteById(Long id) {
        tipoLocalRepository.deleteById(id);
    }
}

