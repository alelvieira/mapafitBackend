package com.mapadavida.mdvBackend.controllers;

import com.mapadavida.mdvBackend.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private UsuarioService usuarioService;

    @DeleteMapping("/usuarios/{userId}")
    public ResponseEntity<Void> clearUserCache(@PathVariable Long userId) {
        usuarioService.clearUserCache(userId, null);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/usuarios")
    public ResponseEntity<Void> clearAllUsersCache() {
        usuarioService.clearAllUsersCache();
        return ResponseEntity.ok().build();
    }
}
