package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.entities.Checkin;
import com.mapadavida.mdvBackend.models.entities.Local;
import com.mapadavida.mdvBackend.models.entities.TipoAtividade;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.repositories.CheckinRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.time.Duration;

@RestController
@RequestMapping("/api/checkins")
public class CheckinController {

    private final CheckinRepository checkinRepository;

    public CheckinController(CheckinRepository checkinRepository) {
        this.checkinRepository = checkinRepository;
    }

    @PostMapping("/checkin")
    public ResponseEntity<Checkin> checkIn(
            @RequestParam Long userId,
            @RequestParam Long localId,
            @RequestParam Long activityTypeId) {

        // Criar um novo objeto Checkin
        Checkin checkin = new Checkin();
        checkin.setUsuario(new Usuario(userId)); // Assumindo que você tem um construtor ou método para isso
        checkin.setLocal(new Local(localId));
        checkin.setTipoAtividade(new TipoAtividade(activityTypeId));
        checkin.setInicio(LocalDateTime.now());

        // Salvar o objeto Checkin no banco de dados e obter o objeto salvo com ID gerado
        Checkin savedCheckin = checkinRepository.save(checkin);

        // Retornar o objeto salvo como resposta
        return ResponseEntity.ok(savedCheckin);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Checkin> checkOut(@RequestParam Long checkinId) {
        // Buscar o checkin pelo ID
        Checkin checkin = checkinRepository.findById(checkinId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checkin not found"));

        // Definir o horário de fim e atualizar o checkin
        checkin.setFim(LocalDateTime.now());
        Checkin updatedCheckin = checkinRepository.save(checkin);

        // Retornar o objeto atualizado como resposta
        return ResponseEntity.ok(updatedCheckin);
    }

    @GetMapping("/checkin-status")
    public ResponseEntity<String> checkInStatus(@RequestParam Long checkinId) {
        // Buscar o checkin pelo ID
        Checkin checkin = checkinRepository.findById(checkinId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checkin not found"));

        // Verificar o status do checkin
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(checkin.getInicio(), now);

        if (checkin.getFim() != null) {
            return ResponseEntity.ok("Checked out already");
        } else if (duration.toMinutes() >= 10) {
            return ResponseEntity.ok("Enable checkout");
        } else {
            return ResponseEntity.ok("Still within check-in time");
        }
    }
}
