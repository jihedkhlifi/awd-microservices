package tn.esprit.autoloc.getway;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "eureka.client.enabled=false")
class GetWayApplicationTests {
    @Autowired
    private RouteLocator routes;

    @Autowired
    private Environment environment;

    @Autowired
    private LoadBalancerClientFactory loadBalancers;

    @Test
    void loadsEachRouteOnceWithTheSelectedDestinationScheme() {
        var configuredRoutes = routes.getRoutes().collectList().block(Duration.ofSeconds(5));
        assertThat(configuredRoutes).extracting(Route::getId).containsExactlyInAnyOrder(
                "candidat-docs", "job-docs", "candidat", "job", "candidature", "meeting", "notification");

        String expectedScheme = environment.acceptsProfiles(Profiles.of("static")) ? "http" : "lb";
        assertThat(configuredRoutes).allSatisfy(route -> assertThat(route.getUri().getScheme()).isEqualTo(expectedScheme));
    }

    @Test
    void randomProfileOnlyChangesTheCandidatLoadBalancer() {
        Class<?> candidatStrategy = environment.acceptsProfiles(Profiles.of("random-candidat & !static"))
                ? RandomLoadBalancer.class : RoundRobinLoadBalancer.class;
        assertThat(loadBalancers.getInstance("candidat")).isInstanceOf(candidatStrategy);
        assertThat(loadBalancers.getInstance("job")).isInstanceOf(RoundRobinLoadBalancer.class);
    }
}
