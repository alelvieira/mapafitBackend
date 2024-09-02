package com.mapadavida.mdvBackend.services;

import com.mapadavida.mdvBackend.models.entities.Local;
import com.mapadavida.mdvBackend.repositories.LocaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocaisService {

    @Autowired
    private LocaisRepository locaisRepository;

    public List<Local> getLocais() {
        return  locaisRepository.findAll();
    }

    public Optional<Local> getLocalById(Long id) {
        return locaisRepository.findById(id);
    }
}
