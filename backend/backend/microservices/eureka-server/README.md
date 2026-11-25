# eureka-server

Serveur de découverte Netflix Eureka (Spring Boot 3.5 / Spring Cloud 2025.0).

```bash
mvn spring-boot:run      # ou ./mvnw spring-boot:run
```
- Dashboard : http://localhost:8761
- Registre (API REST) : http://localhost:8761/eureka/apps (`Accept: application/json` pour du JSON)

À démarrer **avant** les autres microservices.
