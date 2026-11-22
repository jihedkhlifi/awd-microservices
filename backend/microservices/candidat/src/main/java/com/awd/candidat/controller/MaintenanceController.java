package com.awd.candidat.controller;

import com.awd.candidat.health.CandidatHealthIndicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Permet de simuler une panne de Candidat pour observer, dans /actuator/health puis dans Eureka,
 * le passage UP -> OUT_OF_SERVICE -> UP (Candidat utilise H2 : on ne peut pas "couper MySQL" comme pour Job).
 */
@RestController
@RequestMapping("/api/maintenance")
@Tag(name = "Maintenance", description = "Mettre le service en maintenance (démo Actuator / Eureka)")
public class MaintenanceController {

    private final CandidatHealthIndicator healthIndicator;

    public MaintenanceController(CandidatHealthIndicator healthIndicator) {
        this.healthIndicator = healthIndicator;
    }

    @GetMapping
    @Operation(summary = "Lire l'état de maintenance")
    public Map<String, Boolean> get() {
        return Map.of("maintenance", healthIndicator.isMaintenance());
    }

    @PutMapping
    @Operation(summary = "Activer / désactiver la maintenance",
            description = "enabled=true : /actuator/health renvoie OUT_OF_SERVICE (503) et l'instance passe OUT_OF_SERVICE dans Eureka")
    public Map<String, Boolean> set(@RequestParam boolean enabled) {
        healthIndicator.setMaintenance(enabled);
        return Map.of("maintenance", enabled);
    }
}
