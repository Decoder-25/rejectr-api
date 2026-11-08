package dev.decoder.rejectr.controller;

import dev.decoder.rejectr.dto.CreateShredRequest;
import dev.decoder.rejectr.dto.ShredResponse;
import dev.decoder.rejectr.entity.Shred;
import dev.decoder.rejectr.service.ShredService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/shreds")
public class ShredController {

    private final ShredService service;

    public ShredController(ShredService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShredResponse> create(@Valid @RequestBody CreateShredRequest request) {
        Shred saved = service.create(request);

        return ResponseEntity
                .created(URI.create("/shreds/" + saved.getId()))
                .body(ShredResponse.from(saved));
    }
}
