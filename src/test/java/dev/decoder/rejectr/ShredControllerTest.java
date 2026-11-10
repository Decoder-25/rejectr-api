package dev.decoder.rejectr;

import dev.decoder.rejectr.controller.ShredController;
import dev.decoder.rejectr.dto.CreateShredRequest;
import dev.decoder.rejectr.entity.Shred;
import dev.decoder.rejectr.exception.InvalidDeviceTokenException;
import dev.decoder.rejectr.service.ShredService;
import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Reason;
import dev.decoder.rejectr.shred.Stage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.cache.autoconfigure.CacheAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShredController.class)
@ImportAutoConfiguration(CacheAutoConfiguration.class)
class ShredControllerTest {

    private static final UUID ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final String TOKEN = "device-token-1234567890";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShredService service;

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        when(service.create(any(CreateShredRequest.class), any())).thenReturn(saved());

        mockMvc.perform(post("/shreds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stage":"APPLIED_NEVER_HEARD_BACK","outcome":"GHOSTED"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/shreds/" + ID))
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.stage").value("APPLIED_NEVER_HEARD_BACK"))
                .andExpect(jsonPath("$.outcome").value("GHOSTED"))
                .andExpect(jsonPath("$.reason").value("NO_REASON_GIVEN"));
    }

    @Test
    void createPassesDeviceTokenHeader() throws Exception {
        when(service.create(any(CreateShredRequest.class), eq(TOKEN))).thenReturn(saved());

        mockMvc.perform(post("/shreds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Device-Token", TOKEN)
                        .content("""
                                {"stage":"APPLIED_NEVER_HEARD_BACK","outcome":"GHOSTED"}
                                """))
                .andExpect(status().isCreated());

        verify(service).create(any(CreateShredRequest.class), eq(TOKEN));
    }

    @Test
    void createIgnoresUnknownStoryTextField() throws Exception {
        when(service.create(any(CreateShredRequest.class), any())).thenReturn(saved());

        mockMvc.perform(post("/shreds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stage":"APPLIED_NEVER_HEARD_BACK","outcome":"GHOSTED","text":"secret story"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void missingStageReturns400WithFieldError() throws Exception {
        mockMvc.perform(post("/shreds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"outcome":"GHOSTED"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.stage").value("stage is required"));

        verify(service, never()).create(any(), any());
    }

    @Test
    void invalidEnumReturns400MalformedBody() throws Exception {
        mockMvc.perform(post("/shreds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stage":"NOT_A_STAGE","outcome":"GHOSTED"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed request body"));
    }

    @Test
    void shortDeviceTokenReturns400() throws Exception {
        when(service.create(any(CreateShredRequest.class), eq("short")))
                .thenThrow(new InvalidDeviceTokenException("device token must be between 16 and 128 characters"));

        mockMvc.perform(post("/shreds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Device-Token", "short")
                        .content("""
                                {"stage":"APPLIED_NEVER_HEARD_BACK","outcome":"GHOSTED"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("device token must be between 16 and 128 characters"));
    }

    private static Shred saved() {
        Shred shred = new Shred();
        shred.setId(ID);
        shred.setStage(Stage.APPLIED_NEVER_HEARD_BACK);
        shred.setOutcome(Outcome.GHOSTED);
        shred.setReason(Reason.NO_REASON_GIVEN);
        shred.setCreatedAt(Instant.parse("2026-11-10T12:00:00Z"));
        return shred;
    }
}
