package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.entities.Checkin;
import com.mapadavida.mdvBackend.models.entities.Local;
import com.mapadavida.mdvBackend.models.entities.TipoAtividade;
import com.mapadavida.mdvBackend.models.entities.Usuario;
import com.mapadavida.mdvBackend.repositories.CheckinRepository;
import com.mapadavida.mdvBackend.services.LocaisService;
import com.mapadavida.mdvBackend.services.TipoAtividadeService;
import com.mapadavida.mdvBackend.services.UsuarioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Optional;

@RestController
@RequestMapping("/api/checkins")
public class CheckinController {

    private final CheckinRepository checkinRepository;
    private final UsuarioService usuarioService;
    private final LocaisService locaisService;
    private final TipoAtividadeService tipoAtividadeService;

    public CheckinController(CheckinRepository checkinRepository, UsuarioService usuarioService, LocaisService locaisService, TipoAtividadeService tipoAtividadeService) {
        this.checkinRepository = checkinRepository;
        this.usuarioService = usuarioService;
        this.locaisService = locaisService;
        this.tipoAtividadeService = tipoAtividadeService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<Checkin> checkIn(
            @RequestParam Long userId,
            @RequestParam Long localId,
            @RequestParam Long tipoAtividadeId) {

        Optional<Local> local = locaisService.getLocalById(localId);
        Optional<Usuario> usuario = usuarioService.getUsuarioById(userId);
        Optional<TipoAtividade> tipoAtividade = tipoAtividadeService.findById(tipoAtividadeId);

        if (local.isPresent() && usuario.isPresent() && tipoAtividade.isPresent()) {
            Checkin checkin = new Checkin();
            checkin.setUsuario(usuario.get());
            checkin.setTipoAtividade(tipoAtividade.get());
            checkin.setLocal(local.get());
            checkinRepository.save(checkin);
            checkin.setInicio(LocalDateTime.now());

            Checkin savedCheckin = checkinRepository.save(checkin);

            return ResponseEntity.ok(savedCheckin);

        }
        return ResponseEntity.notFound().build();
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
