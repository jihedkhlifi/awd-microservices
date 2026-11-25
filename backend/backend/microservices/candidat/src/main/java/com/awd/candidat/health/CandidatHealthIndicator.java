package com.awd.candidat.health;

import com.awd.candidat.repository.CandidateRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * HealthIndicator personnalisé (atelier Observabilité).
 *
 * Apparaît dans /actuator/health sous le composant "candidat" (nom du bean sans le suffixe HealthIndicator).
 * - UP             : la table des candidats répond, on publie le nombre de candidats.
 * - OUT_OF_SERVICE : le service a été mis en maintenance (PUT /api/maintenance?enabled=true).
 * - DOWN           : la base ne répond plus.
 *
 * Avec eureka.client.healthcheck.enabled=true, ce statut est recopié dans le dashboard Eureka.
 */
@Component
public class CandidatHealthIndicator implements HealthIndicator {

    private final CandidateRepository candidateRepository;
    private final AtomicBoolean maintenance = new AtomicBoolean(false);

    public CandidatHealthIndicator(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @Override
    public Health health() {
        if (maintenance.get()) {
            return Health.outOfService()
                    .withDetail("maintenance", true)
                    .withDetail("message", "Service retiré volontairement (maintenance)")
                    .build();
        }
        try {
            return Health.up()
                    .withDetail("maintenance", false)
                    .withDetail("candidates", candidateRepository.count())
                    .build();
        } catch (Exception e) {
            return Health.down(e).withDetail("maintenance", false).build();
        }
    }

    public boolean isMaintenance() {
        return maintenance.get();
    }

    public void setMaintenance(boolean enabled) {
        maintenance.set(enabled);
    }
}
