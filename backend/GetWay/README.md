# Atelier API Gateway — chapitre 5

La Gateway Spring Cloud WebFlux fournit un point d'entrée sur `http://localhost:8086`. Le port `8085` des exemples du chapitre est déjà occupé par Candidature, et le port `8080` est utilisé par Oracle sur ce poste. Eureka écoute sur `8761`.

## Démarrage et méthodes de routage

Démarrer les services à tester selon le [guide du backend](../README.md). En mode dynamique, démarrer Eureka en premier et vérifier que les services sont `UP` dans son tableau de bord. Exécuter une des commandes suivantes depuis `GetWay`.

| Mode | Commande | Déclaration | Destination |
|---|---|---|---|
| Dynamique en properties (par défaut) | `mvn spring-boot:run` | `application.properties` | `lb://nom-du-service` |
| Dynamique en Java | `mvn spring-boot:run "-Dspring-boot.run.profiles=eureka-java"` | `GatewayRoutes.java`, bean `RouteLocator` | `lb://nom-du-service` |
| Statique en properties | `mvn spring-boot:run "-Dspring-boot.run.profiles=static-properties"` | `application.properties` | URL HTTP fixe |
| Statique en Java | `mvn spring-boot:run "-Dspring-boot.run.profiles=static-java"` | `GatewayRoutes.java`, bean `RouteLocator` | URL HTTP fixe |

En mode dynamique, la Gateway s'enregistre sous `GATEWAY`, lit le registre Eureka et sélectionne une instance via Spring Cloud LoadBalancer. Les modes statiques désactivent le client Eureka de la Gateway et utilisent les ports ci-dessous.

| Service | Chemins via la Gateway | Port statique |
|---|---|---:|
| Candidat | `/mic1/candidates/**`, `/mic1/addresses/**` | 8081 |
| Job | `/api/jobs/**`, `/api/categories/**` | 8082 |
| Candidature | `/mic3/candidature/**` | 8085 |
| Meeting | `/api/meetings/**` | 8083 |
| Notification | `/api/notifications/**` | 8084 |

## Swagger et requêtes HTTP

Ouvrir Swagger UI lorsque Candidat et Job sont démarrés. Le menu permet de choisir l'une des deux API. Les routes `/candidat/v3/api-docs` et `/jobs/v3/api-docs` appliquent `RewritePath` pour atteindre le `/v3/api-docs` du service correspondant. Le serveur OpenAPI relatif permet à **Try it out** de passer par la Gateway. [Swagger UI](http://localhost:8086/swagger-ui.html)

Dans Postman, utiliser `GET` et les URL ci-dessous. Ces commandes PowerShell effectuent les mêmes appels.

```powershell
Invoke-RestMethod http://localhost:8086/mic1/candidates
Invoke-RestMethod http://localhost:8086/api/jobs
Invoke-RestMethod http://localhost:8086/mic3/candidature/hello
Invoke-RestMethod http://localhost:8086/api/meetings/hello
Invoke-RestMethod http://localhost:8086/api/notifications/hello
```

## Rendu de la partie 3

### 2.b — Stratégie par défaut

Lancer une instance supplémentaire de Candidat depuis `backend/microservices/candidat`.

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8091"
```

Lorsque les deux instances Candidat sont `UP` dans Eureka, lancer la Gateway avec les logs de routage.

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=routing-logs"
```

Envoyer des requêtes consécutives depuis un autre terminal.

```powershell
1..10 | ForEach-Object {
    Invoke-RestMethod http://localhost:8086/mic1/candidates | Out-Null
}
```

La stratégie **Round Robin** sélectionne les instances à tour de rôle. Relever les ports dans les lignes `LoadBalancerClientFilter url chosen` de la console. L'ordre de départ dépend de l'état du répartiteur.

Sur 16 requêtes HTTP de vérification avec deux instances simulées déclarées au client Eureka, les logs ont alterné entre `8091` et `8081` à chaque requête.

### 3 — Random pour Candidat

| Stratégie | Principe |
|---|---|
| Round Robin | Sélectionne chaque instance à tour de rôle. |
| Random | Tire une instance parmi celles disponibles à chaque requête. |
| Pondérée | Favorise les instances auxquelles un poids plus élevé est attribué. |
| Affinité de zone | Privilégie les instances situées dans la zone du client. |

Redémarrer la Gateway avec le profil suivant et relancer la même série de requêtes.

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=random-candidat,routing-logs"
```

Avec `@LoadBalancerClient(name = "candidat")`, seul Candidat utilise `RandomLoadBalancer`. Les autres services conservent `RoundRobinLoadBalancer`. Dans le même test HTTP, les trois premières requêtes ont atteint `8091`, puis les cinq suivantes ont atteint `8081`. [Stratégies Spring Cloud LoadBalancer](https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/loadbalancer.html)

Les tests Maven vérifient les routes et la portée de la configuration Random.

```powershell
mvn test
mvn test "-Dspring.profiles.active=random-candidat"
```
