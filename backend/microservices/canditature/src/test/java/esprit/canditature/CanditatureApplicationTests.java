package esprit.canditature;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Pas de serveur Eureka pendant les tests
@SpringBootTest(properties = "eureka.client.enabled=false")
class CanditatureApplicationTests {

	@Test
	void contextLoads() {
	}

}
