package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.entities.Endereco;
import com.mapadavida.mdvBackend.models.entities.Local;
import com.mapadavida.mdvBackend.repositories.EnderecoRepository;
import com.mapadavida.mdvBackend.repositories.LocaisRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@CrossOrigin
@RestController
@RequestMapping("/locais")
public class LocaisController {

    @Autowired
    private LocaisRepository locaisRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;
    private ModelMapper modelMapper = new ModelMapper();

    @PostMapping(value = "/consultar")
    public ResponseEntity<Optional<Local>> consultar(@RequestBody Endereco endereco) {

        Optional<Local> localEx = localEnderecoExistente(endereco);

        if (localEx.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).body(localEx);

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping(value = "/indicar")
    public ResponseEntity<Optional<Local>> indicar(@RequestBody Local local) {
        Optional<Local> localEx = localExistente(local);


        if (localEx.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } else if (local.getEndereco() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    public Optional<Local> localEnderecoExistente(Endereco endereco) {
        Optional<Local> localEx = Optional.ofNullable(locaisRepository.findByEndereco(endereco));
        return localEx;
    }

    public Optional<Local> localExistente(Local local) {
        Optional<Local> localEx = locaisRepository.findById(local.getId());
        return localEx;
    }

}

