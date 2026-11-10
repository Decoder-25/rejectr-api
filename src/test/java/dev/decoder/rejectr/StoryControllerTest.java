package dev.decoder.rejectr;

import dev.decoder.rejectr.controller.StoryController;
import dev.decoder.rejectr.dto.CreateStoryRequest;
import dev.decoder.rejectr.entity.Story;
import dev.decoder.rejectr.exception.InvalidDeviceTokenException;
import dev.decoder.rejectr.service.StoryService;
import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Stage;
import dev.decoder.rejectr.shred.Status;
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

@WebMvcTest(StoryController.class)
@ImportAutoConfiguration(CacheAutoConfiguration.class)
class StoryControllerTest {

    private static final UUID ID = UUID.fromString("99999999-8888-7777-6666-555555555555");
    private static final String TOKEN = "device-token-1234567890";
    private static final String VALID_BODY = """
            {"stage":"FINAL_ROUND","outcome":"GHOSTED","body":"Five rounds then silence."}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StoryService service;

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        when(service.create(any(CreateStoryRequest.class), any())).thenReturn(saved());

        mockMvc.perform(post("/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/stories/" + ID))
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.stage").value("FINAL_ROUND"))
                .andExpect(jsonPath("$.outcome").value("GHOSTED"))
                .andExpect(jsonPath("$.body").value("Five rounds then silence."))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void createPassesDeviceTokenHeader() throws Exception {
        when(service.create(any(CreateStoryRequest.class), eq(TOKEN))).thenReturn(saved());

        mockMvc.perform(post("/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Device-Token", TOKEN)
                        .content(VALID_BODY))
                .andExpect(status().isCreated());

        verify(service).create(any(CreateStoryRequest.class), eq(TOKEN));
    }

    @Test
    void blankBodyReturns400WithFieldError() throws Exception {
        mockMvc.perform(post("/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stage":"FINAL_ROUND","outcome":"GHOSTED","body":"   "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.body").value("story must not be empty"));

        verify(service, never()).create(any(), any());
    }

    @Test
    void personalDetailsInBodyReturns400WithFieldError() throws Exception {
        mockMvc.perform(post("/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stage":"FINAL_ROUND","outcome":"GHOSTED","body":"email me at jane@example.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.body")
                        .value("remove email addresses and phone numbers before sharing"));

        verify(service, never()).create(any(), any());
    }

    @Test
    void missingStageReturns400WithFieldError() throws Exception {
        mockMvc.perform(post("/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"outcome":"GHOSTED","body":"A story."}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.stage").value("stage is required"));

        verify(service, never()).create(any(), any());
    }

    @Test
    void shortDeviceTokenReturns400() throws Exception {
        when(service.create(any(CreateStoryRequest.class), eq("short")))
                .thenThrow(new InvalidDeviceTokenException("device token must be between 16 and 128 characters"));

        mockMvc.perform(post("/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Device-Token", "short")
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("device token must be between 16 and 128 characters"));
    }

    private static Story saved() {
        Story story = new Story();
        story.setId(ID);
        story.setStage(Stage.FINAL_ROUND);
        story.setOutcome(Outcome.GHOSTED);
        story.setBody("Five rounds then silence.");
        story.setStatus(Status.PUBLISHED);
        story.setCreatedAt(Instant.parse("2026-11-10T12:00:00Z"));
        return story;
    }
}
