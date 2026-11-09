package dev.decoder.rejectr.controller;

import dev.decoder.rejectr.dto.WeeklyStatsResponse;
import dev.decoder.rejectr.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stats", description = "Aggregate rejection statistics")
@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @Operation(
            summary = "Weekly outcome stats",
            description = "Shred counts per outcome over the last 7 days, plus the total.",
            responses = @ApiResponse(responseCode = "200", description = "Counts by outcome")
    )
    @GetMapping("/weekly")
    public WeeklyStatsResponse weekly() {
        return service.weekly();
    }
}
