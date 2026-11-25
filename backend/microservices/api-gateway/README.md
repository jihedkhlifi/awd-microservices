# API Gateway – Spring Cloud Gateway (port 8888)

Point d'entrée unique du JobBoard : le client n'appelle plus chaque microservice sur son port,
il appelle **uniquement** `http://localhost:8888` et la Gateway transfère la requête.

| Préfixe appelé sur la Gateway | Microservice | Statique (Partie 2) | Dynamique (Partie 3) |
|---|---|---|---|
| `/mic1/**` | candidat | `http://localhost:8081` | `lb://candidat` |
| `/api/jobs/**`, `/api/categories/**` | job | `http://localhost:8082` | `lb://job` |
| `/api/meetings/**` | meeting | `http://localhost:8083` | `lb://meeting` |
| `/api/notifications/**` | notification | `http://localhost:8084` | `lb://notification` |
| `/mic3/**` | candidature | `http://localhost:8085` | `lb://candidature` |

## Versions

Spring Boot 3.5.6 + Spring Cloud 2025.0.0 (même couple que `eureka-server`).
Différences avec le PDF de l'atelier, dues à Spring Cloud 2025.0 :

| PDF | Ce projet | Pourquoi |
|---|---|---|
| `spring-cloud-starter-gateway` | `spring-cloud-starter-gateway-server-webflux` | starter renommé (l'ancien est déprécié) |
| `spring.cloud.gateway.routes[...]` | `spring.cloud.gateway.server.webflux.routes[...]` | préfixe des propriétés renommé |
| `@EnableEurekaClient` | `@EnableDiscoveryClient` | `@EnableEurekaClient` a été supprimé |
| port 8085 | port 8888 | 8085 est déjà utilisé par `candidature` |

## Choisir la méthode de routage (profils Spring)

Les 4 méthodes de l'atelier sont toutes dans le projet ; on en active une avec un profil :

| Profil | Partie | Où sont les routes |
|---|---|---|
| `static-java` (par défaut) | 2 – Méthode 1 | `GatewayApplication#staticRoutes` |
| `static-props` | 2 – Méthode 2 | `application-static-props.properties` |
| `dynamic-java` | 3 – Méthode 1 | `GatewayApplication#dynamicRoutes` |
| `dynamic-props` | 3 – Méthode 2 | `application-dynamic-props.properties` |
| `dynamic-java,random-lb` | 3 – Q3 | + `RandomLoadBalancerActivation` (Random pour candidat) |

- **IntelliJ** : Run > Edit Configurations > `GatewayApplication` > *Active profiles*.
- **Terminal** : `./mvnw spring-boot:run -Dspring-boot.run.profiles=dynamic-props`

Routes actives : `http://localhost:8888/actuator/gateway/routes`

## Tests à travers la Gateway

```bash
curl http://localhost:8888/mic1/candidates            # candidat
curl http://localhost:8888/api/jobs                   # job (MySQL démarré)
curl http://localhost:8888/api/categories             # job
curl http://localhost:8888/api/meetings/hello         # meeting
curl http://localhost:8888/api/notifications/hello    # notification
curl http://localhost:8888/mic3/candidature/hello     # candidature

curl -X POST http://localhost:8888/mic1/candidates -H "Content-Type: application/json" \
     -d '{"firstname":"Badia","lastname":"Abouhdid","email":"badia@example.com"}'
```

## Partie 3 – Réponses

### Q2.b – Algorithme de load balancing utilisé

Lancer une 2ᵉ instance de candidat (`-Dserver.port=8091`), puis appeler plusieurs fois
`http://localhost:8888/mic1/candidates`. Dans la console de la Gateway, la ligne
`LoadBalancerClientFilter url chosen: http://<ip>:8081/...` puis `...:8091/...` alterne
**8081 → 8091 → 8081 → 8091** : c'est l'algorithme **Round Robin**
(`RoundRobinLoadBalancer` de Spring Cloud LoadBalancer, l'algorithme par défaut ;
il a remplacé Netflix Ribbon). Chaque instance est choisie à tour de rôle, dans l'ordre.

### Q3 – Autres types de load balancing et Random pour candidat

Algorithmes classiques : Round Robin, Weighted Round Robin, Random, Least Connections,
Least Response Time, IP Hash, Sticky Session.

Fournis par Spring Cloud LoadBalancer :
- `RoundRobinLoadBalancer` (défaut) et `RandomLoadBalancer` ;
- des filtres sur la liste d'instances (`ServiceInstanceListSupplier`) : *weighted*, *zone preference*,
  *health-check*, *same instance preference*, *request-based sticky session*, *hint-based* ;
- tout autre algorithme (ex. Least Connections) en écrivant son propre `ReactorServiceInstanceLoadBalancer`.

Configuration Random **uniquement pour candidat** :

1. `com.awd.lbconfig.CandidatRandomLoadBalancerConfig` déclare un bean `RandomLoadBalancer`.
   La classe n'a **pas** `@Configuration` et est **hors** du package scanné, sinon elle s'appliquerait à tous les services.
2. `RandomLoadBalancerActivation` porte `@LoadBalancerClient(name = "candidat", configuration = CandidatRandomLoadBalancerConfig.class)` :
   seul `lb://candidat` utilise Random, les autres services restent en Round Robin.

Test : profils `dynamic-java,random-lb`, même scénario qu'en Q2.b → l'ordre devient irrégulier
(ex. 8091, 8091, 8081, 8091, 8081, 8081…).

## Statique ou dynamique ?

| | Statique (`http://localhost:8081`) | Dynamique (`lb://candidat`) |
|---|---|---|
| Eureka | non nécessaire | obligatoire |
| Avantages | simple, rapide, marche sans Eureka | pas d'adresse en dur, load balancing, scalabilité |
| Inconvénients | si l'IP/port change, la route casse ; une seule instance | dépend d'Eureka, délai de découverte au démarrage |
