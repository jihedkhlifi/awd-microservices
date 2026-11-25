package esprit.canditature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient   // client Eureka (facultatif avec la dépendance, gardé pour la lisibilité)
public class CanditatureApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanditatureApplication.class, args);
	}

}
