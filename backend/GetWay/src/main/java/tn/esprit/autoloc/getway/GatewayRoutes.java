package tn.esprit.autoloc.getway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("java-routes")
class GatewayRoutes {

    @Bean
    RouteLocator javaRouteLocator(RouteLocatorBuilder builder,
                                  @Value("${gateway.services.candidat}") String candidat,
                                  @Value("${gateway.services.job}") String job,
                                  @Value("${gateway.services.candidature}") String candidature,
                                  @Value("${gateway.services.meeting}") String meeting,
                                  @Value("${gateway.services.notification}") String notification) {
        return builder.routes()
                .route("candidat-docs", route -> route.path("/candidat/v3/api-docs")
                        .filters(filters -> filters.rewritePath("/candidat/v3/api-docs", "/v3/api-docs"))
                        .uri(candidat))
                .route("job-docs", route -> route.path("/jobs/v3/api-docs")
                        .filters(filters -> filters.rewritePath("/jobs/v3/api-docs", "/v3/api-docs"))
                        .uri(job))
                .route("candidat", route -> route.path("/mic1/candidates/**", "/mic1/addresses/**").uri(candidat))
                .route("job", route -> route.path("/api/jobs/**", "/api/categories/**").uri(job))
                .route("candidature", route -> route.path("/mic3/candidature/**").uri(candidature))
                .route("meeting", route -> route.path("/api/meetings/**").uri(meeting))
                .route("notification", route -> route.path("/api/notifications/**").uri(notification))
                .build();
    }
}
