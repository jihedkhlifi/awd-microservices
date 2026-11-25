package com.awd.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * API Gateway du JobBoard - point d'entrée unique : http://localhost:8888
 *
 * Les 4 façons de déclarer les routes (atelier API Gateway) sont dans le projet.
 * On choisit celle à utiliser avec un profil Spring :
 *
 *   static-java    Partie 2 - Méthode 1 : routes statiques dans cette classe (uri http://localhost:port)
 *   static-props   Partie 2 - Méthode 2 : routes statiques dans application-static-props.properties
 *   dynamic-java   Partie 3 - Méthode 1 : routes dynamiques dans cette classe (uri lb://nom-eureka)
 *   dynamic-props  Partie 3 - Méthode 2 : routes dynamiques dans application-dynamic-props.properties
 *   random-lb      Partie 3 - Q3 : algorithme Random pour le service candidat uniquement
 *                  (à combiner avec un profil dynamique, ex. "dynamic-java,random-lb")
 *
 * NB : @EnableEurekaClient (cité dans le PDF) n'existe plus depuis Spring Cloud 2022.
 *      On utilise @EnableDiscoveryClient (facultatif : la dépendance Eureka suffit).
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    // ===================== Partie 2 - Méthode 1 : configuration STATIQUE (Java) =====================
    // La Gateway pointe directement vers l'adresse du microservice : Eureka n'est pas utilisé.
    @Bean
    @Profile("static-java")
    public RouteLocator staticRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("candidat", r -> r.path("/mic1/**")
                        .uri("http://localhost:8081"))
                .route("job", r -> r.path("/api/jobs/**", "/api/categories/**")
                        .uri("http://localhost:8082"))
                .route("meeting", r -> r.path("/api/meetings/**")
                        .uri("http://localhost:8083"))
                .route("notification", r -> r.path("/api/notifications/**")
                        .uri("http://localhost:8084"))
                .route("candidature", r -> r.path("/mic3/**")
                        .uri("http://localhost:8085"))
                .build();
    }

    // ===================== Partie 3 - Méthode 1 : configuration DYNAMIQUE (Java) ====================
    // lb://<nom> : la Gateway demande à Eureka les instances du service, puis le LoadBalancer en choisit une.
    @Bean
    @Profile("dynamic-java")
    public RouteLocator dynamicRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("candidat", r -> r.path("/mic1/**")
                        .uri("lb://candidat"))
                .route("job", r -> r.path("/api/jobs/**", "/api/categories/**")
                        .uri("lb://job"))
                .route("meeting", r -> r.path("/api/meetings/**")
                        .uri("lb://meeting"))
                .route("notification", r -> r.path("/api/notifications/**")
                        .uri("lb://notification"))
                .route("candidature", r -> r.path("/mic3/**")
                        .uri("lb://candidature"))
                .build();
    }
}
