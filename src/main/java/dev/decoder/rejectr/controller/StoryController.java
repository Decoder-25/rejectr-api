package dev.decoder.rejectr.controller;

import dev.decoder.rejectr.dto.CreateStoryRequest;
import dev.decoder.rejectr.dto.StoryResponse;
import dev.decoder.rejectr.entity.Story;
import dev.decoder.rejectr.service.StoryService;
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

@Tag(name = "Stories", description = "Publish an anonymous, scrubbed rejection story to the wall")
@RestController
@RequestMapping("/stories")
public class StoryController {

    private final StoryService service;

    public StoryController(StoryService service) {
        this.service = service;
    }

    @Operation(
            summary = "Publish a story",
            description = "Stores the stage, outcome and scrubbed body of a story and publishes it to "
                    + "the wall. The optional X-Device-Token header is hashed with HMAC-SHA256 before "
                    + "storage; the raw token is never persisted.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Story published"),
                    @ApiResponse(responseCode = "400", description = "Validation failed or invalid value")
            }
    )
    @PostMapping
    public ResponseEntity<StoryResponse> create(
            @Parameter(description = "Opaque token that identifies a returning browser", in = ParameterIn.HEADER)
            @RequestHeader(value = "X-Device-Token", required = false) String deviceToken,
            @Valid @RequestBody CreateStoryRequest request) {
        Story saved = service.create(request, deviceToken);

        return ResponseEntity
                .created(URI.create("/stories/" + saved.getId()))
                .body(StoryResponse.from(saved));
    }
}
