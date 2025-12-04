package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.models.dto.CheckinDTO;
import com.mapadavida.mdvBackend.models.entities.Checkin;
import com.mapadavida.mdvBackend.repositories.CheckinRepository;
import com.mapadavida.mdvBackend.services.CheckinService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/checkins")
@CrossOrigin
public class CheckinController {

    @Autowired
    private CheckinService checkinService;
    @Autowired
    private CheckinRepository checkinRepository; // Kept for read-only status check

    @PostMapping("/checkin")
    public ResponseEntity<CheckinDTO> checkIn(
            @RequestParam Long userId,
            @RequestParam Long localId,
            @RequestParam Long tipoAtividadeId) {
        try {
            CheckinDTO checkinDTO = checkinService.performCheckIn(userId, localId, tipoAtividadeId);
            return ResponseEntity.ok(checkinDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckinDTO> checkOut(@RequestParam Long checkinId) {
        try {
            CheckinDTO updatedCheckinDTO = checkinService.performCheckOut(checkinId);
            return ResponseEntity.ok(updatedCheckinDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/checkin-status")
    public ResponseEntity<Map<String, String>> checkInStatus(@RequestParam Long checkinId) {
        Checkin checkin = checkinRepository.findById(checkinId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checkin not found"));

        LocalDateTime now = LocalDateTime.now();

        if (checkin.getFim() != null) {
            return ResponseEntity.ok(Map.of("status", "Checked out already"));
        }

        Duration duration = Duration.between(checkin.getInicio(), now);
        if (duration.toMinutes() >= 1) {

            return ResponseEntity.ok(Map.of("status", "Enable checkout"));
        } else {
            return ResponseEntity.ok(Map.of("status","Still within check-in time"));
        }
    }

    @GetMapping
    public ResponseEntity<List<CheckinDTO>> checkIn(
            @RequestParam Long userId) {
        try {
            List<CheckinDTO> checkinDTO = checkinService.findCheckIns(userId);
            return ResponseEntity.ok(checkinDTO);
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
