package dev.decoder.rejectr;

import dev.decoder.rejectr.controller.StatsController;
import dev.decoder.rejectr.dto.OutcomeCount;
import dev.decoder.rejectr.service.StatsService;
import dev.decoder.rejectr.shred.Outcome;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.cache.autoconfigure.CacheAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
@ImportAutoConfiguration(CacheAutoConfiguration.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatsService service;

    @Test
    void weeklyReturnsListOfTypeAndCount() throws Exception {
        when(service.weekly()).thenReturn(List.of(
                new OutcomeCount(Outcome.GHOSTED, 3),
                new OutcomeCount(Outcome.AUTOMATED_EMAIL, 1)
        ));

        mockMvc.perform(get("/stats/weekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("GHOSTED"))
                .andExpect(jsonPath("$[0].count").value(3))
                .andExpect(jsonPath("$[1].type").value("AUTOMATED_EMAIL"))
                .andExpect(jsonPath("$[1].count").value(1));

        verify(service).weekly();
    }

    @Test
    void weeklyReturnsEmptyArrayWhenNoShreds() throws Exception {
        when(service.weekly()).thenReturn(List.of());

        mockMvc.perform(get("/stats/weekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
