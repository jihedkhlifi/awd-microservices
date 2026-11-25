package tn.esprit.autoloc.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"eureka.client.enabled=false", "eureka.client.register-with-eureka=false"})
class GatewayApplicationTests {

    @Test
    void contextLoads() {
    }

}
