package com.awd.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Démarre le contexte avec le profil statique (sans Eureka) pour vérifier la configuration.
@SpringBootTest(properties = "spring.profiles.active=static-java")
class GatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}
