package tn.esprit.autoloc.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

        public static void main(String[] args) {
                SpringApplication.run(GatewayApplication.class, args);
        }

        /**
         * Partie 2 - Méthode 1 : Configuration des routes statiques via
         * RouteLocatorBuilder.
         */
        @Bean
        public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
                return builder.routes()
                                // Route statique vers Candidat (port fixe 8081)
                                .route("Candidat", r -> r.path("/candidats/**", "/candidat/**", "/mic1/candidates/**")
                                                .uri("http://localhost:8081"))

                                // Route statique vers Candidature (port fixe 8085)
                                .route("Candidature",
                                                r -> r.path("/candidatures/**", "/candidature/**",
                                                                "/mic3/candidature/**")
                                                                .uri("http://localhost:8085"))

                                .build();
        }
}
