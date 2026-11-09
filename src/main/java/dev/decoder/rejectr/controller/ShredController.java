package dev.decoder.rejectr.controller;

import dev.decoder.rejectr.dto.CreateShredRequest;
import dev.decoder.rejectr.dto.ShredResponse;
import dev.decoder.rejectr.entity.Shred;
import dev.decoder.rejectr.service.ShredService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Shreds", description = "Record a job rejection by category only (no story text)")
@RestController
@RequestMapping("/shreds")
public class ShredController {

    private final ShredService service;

    public ShredController(ShredService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create a shred",
            description = "Stores the stage, outcome and optional reason. Any story text sent by the "
                    + "client is ignored and never persisted. The optional X-Device-Token header is "
                    + "hashed with HMAC-SHA256 before storage; the raw token is never persisted.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Shred created"),
                    @ApiResponse(responseCode = "400", description = "Validation failed or invalid value")
            }
    )
    @PostMapping
    public ResponseEntity<ShredResponse> create(
            @Parameter(description = "Opaque token that identifies a returning browser", in = ParameterIn.HEADER)
            @RequestHeader(value = "X-Device-Token", required = false) String deviceToken,
            @Valid @RequestBody CreateShredRequest request) {
        Shred saved = service.create(request, deviceToken);

        return ResponseEntity
                .created(URI.create("/shreds/" + saved.getId()))
                .body(ShredResponse.from(saved));
    }
}
