# AWD – Microservices : Eureka + Observabilité (Spring Boot Actuator)

Projet réalisé dans le cadre du module **Architecture Web Distribuée** (2026-2027) :
- **Atelier 1** : Implémentation d'un serveur de découverte **Eureka**
- **Atelier 2** : Observabilité avec **Spring Boot Actuator**

## 👥 Équipe

| Nom         | Prénom     | Classe |
|-------------|------------|---|
| _Khlifi_    | _jihed_    | _Classe_ |
| _Ayedi_     | _ahmed_    | _Classe_ |
| _Makhloufi_ | _insaf_    | _Classe_ |
| _Weslati_   | _ferdaous_ | _Classe_ |

---

## 🏗️ Architecture

Tous les microservices s'enregistrent dans le même serveur **Eureka** et se retrouvent grâce à leur nom
au lieu d'une adresse écrite en dur.

| Microservice | Technologie | Port | Nom dans Eureka | Endpoints de santé |
|---|---|---|---|---|
| eureka-server | Spring Boot 3.5 + Spring Cloud 2025.0 | 8761 | – | dashboard |
| candidat | Spring Boot 3.2 + H2 | 8081 | CANDIDAT | `/actuator/health`, `/actuator/info`, `/actuator/metrics`, `/actuator/loggers` |
| job | Spring Boot 3.2 + MySQL | 8082 | JOB | idem (+ composant `db` MySQL) |
| candidature | Spring Boot 4.0 | 8085 | CANDIDATURE | idem |
| meeting | Node.js / Express + `eureka-js-client` | 8083 | MEETING | `/health`, `/info` |
| notification | Python / FastAPI + `py-eureka-client` | 8084 | NOTIFICATION | `/health`, `/info` |

```
backend/
├── README.md
├── docs/                       captures d'écran
└── microservices/
    ├── eureka-server/          serveur de découverte (port 8761)
    ├── candidat/               Spring Boot + H2
    ├── job/                    Spring Boot + MySQL
    ├── canditature/            Spring Boot
    ├── meeting/                Node.js / Express
    └── notification/           Python / FastAPI
```

---

## ⚙️ Prérequis

| Outil | Version utilisée |
|---|---|
| JDK | 21 (Microsoft OpenJDK 21) |
| Maven | intégré à IntelliJ IDEA |
| MySQL | XAMPP |
| Node.js | 24 |
| Python | 3.13 |

---

## 🚀 Démarrage

**Ordre de démarrage** : MySQL → Eureka → Candidat, Candidature, Job → Meeting → Notification

1. **MySQL** : démarrer MySQL dans XAMPP, puis importer `microservices/job/database/job_db.sql` dans phpMyAdmin.
2. **Eureka** : lancer `EurekaApplication` → http://localhost:8761
3. **Candidat, Candidature, Job** : lancer `CandidatApplication`, `CanditatureApplication`, `JobApplication`.
4. **Meeting** :
   ```powershell
   cd microservices\meeting
   npm install
   npm start                         # instance 1 (port 8083)
   $env:PORT=8093; npm start         # instance 2 (port 8093), dans un 2e terminal
   ```
5. **Notification** :
   ```powershell
   cd microservices\notification
   py -3.13 -m venv venv
   .\venv\Scripts\Activate.ps1
   pip install -r requirements.txt
   python -m app.main                       # instance 1 (port 8084)
   $env:PORT=8094; python -m app.main       # instance 2 (port 8094), dans un 2e terminal (venv activé)
   ```

---

## 🧭 Atelier 1 – Eureka Server

### Ce qui a été réalisé

**Partie 1 – Serveur Eureka** (`microservices/eureka-server`)
- Dépendance `spring-cloud-starter-netflix-eureka-server`
- Annotation `@EnableEurekaServer` sur `EurekaApplication`
- Configuration :
  ```properties
  spring.application.name=eureka
  server.port=8761
  eureka.client.register-with-eureka=false
  eureka.client.fetch-registry=false
  ```

**Partie 2 – Clients Spring Boot** (Candidat, Job, Candidature)
- Dépendance `spring-cloud-starter-netflix-eureka-client` + annotation `@EnableDiscoveryClient`
- Configuration :
  ```properties
  eureka.client.service-url.defaultZone=http://localhost:8761/eureka
  eureka.client.register-with-eureka=true
  eureka.client.fetch-registry=true
  eureka.instance.prefer-ip-address=true
  eureka.instance.instance-id=${spring.application.name}:${server.port}
  ```
- Le microservice `canditature` a été renommé `candidature` (`spring.application.name`) pour apparaître sous **CANDIDATURE**.

**Partie 3 – Clients Node.js et Python**
- **Meeting** (`src/config/eureka.js`) : `eureka-js-client`, enregistrement sous **MEETING**, port lu dans la variable
  `PORT`, désinscription d'Eureka à l'arrêt (Ctrl+C).
- **Notification** (`app/eureka.py`) : `py-eureka-client`, enregistrement sous **NOTIFICATION** dans le `lifespan`
  de FastAPI, port lu dans la variable `PORT`, `stop_async()` à l'arrêt de l'application.

### Résultat : dashboard Eureka

Les 5 services sont visibles en même temps, avec **2 instances de MEETING** (8083, 8093) et
**2 instances de NOTIFICATION** (8084, 8094) :

![Dashboard Eureka](docs/eureka-dashboard.png)

Les endpoints `hello` continuent de répondre :
- `GET http://localhost:8083/api/meetings/hello` → `{"message":"hello I'm microservice meeting"}`
- `GET http://localhost:8084/api/notifications/hello` → `{"message":"hello I'm microservice notification"}`

---

## 🩺 Atelier 2 – Observabilité avec Spring Boot Actuator

### Ce qui a été réalisé

- Dépendance `spring-boot-starter-actuator` dans Candidat, Job et Candidature.
- Configuration :
  ```properties
  management.endpoints.web.exposure.include=health,info,metrics,loggers
  management.endpoint.health.show-details=always
  eureka.client.healthcheck.enabled=true
  management.info.env.enabled=true

  info.app.name=candidat
  info.app.version=1.0.0
  info.app.description=Microservice de gestion des candidats
  ```
- **HealthIndicator personnalisé** `CandidatHealthIndicator` (Candidat) : composant `candidat` qui publie le nombre de
  candidats, avec un mode maintenance activable par `PUT /api/maintenance?enabled=true|false`.
- **Meeting et Notification** : routes `GET /health` → `{"status":"UP"}` et `GET /info` → nom et version, déclarées
  dans Eureka comme `healthCheckUrl` et `statusPageUrl`.

### 1. `/actuator/health` – Job UP

`GET http://localhost:8082/actuator/health`

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP", "details": { "database": "MySQL", "validationQuery": "isValid()" } },
    "discoveryComposite": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" },
    "refreshScope": { "status": "UP" }
  }
}
```

![Job health UP](docs/job-health-up.png)

### 2. Panne de la base MySQL

Après l'arrêt de MySQL dans XAMPP :

```json
{
  "status": "DOWN",
  "components": {
    "db": { "status": "DOWN",
            "details": { "error": "org.springframework.jdbc.CannotGetJdbcConnectionException: Failed to obtain JDBC Connection" } },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

- Un seul composant DOWN (`db`) suffit à faire passer le service **DOWN** (HTTP 503).
- Grâce à `eureka.client.healthcheck.enabled=true`, **JOB passe DOWN dans le dashboard Eureka**.
- `job` disparaît de la liste des services proposés aux autres (`discoveryClient.services`) : une instance malade
  n'est plus proposée aux autres services.
- Après le redémarrage de MySQL, Job repasse **UP** automatiquement.

![Job health DOWN](docs/job-health-down.png)
![Eureka : JOB DOWN](docs/eureka-job-down.png)

### 3. `/actuator/info`

`GET http://localhost:8081/actuator/info` (aussi accessible en cliquant sur l'instance dans le dashboard Eureka) :

```json
{
  "app": {
    "name": "candidat",
    "version": "1.0.0",
    "description": "Microservice de gestion des candidats",
    "team": "Nom de votre équipe"
  }
}
```

![Candidat info](docs/candidat-info.png)

### 4. `/actuator/metrics`

Après plusieurs appels à `GET http://localhost:8081/mic1/candidates` :

| Mesure | `/actuator/metrics/http.server.requests` | `...?tag=status:200` |
|---|---|---|
| COUNT | **70** | **57** |
| TOTAL_TIME | 1,069 s | 1,007 s |
| MAX | **0,074 s** | 0,074 s |

**Question : Combien de requêtes HTTP Candidat a-t-il traitées ? Quelle est la durée de la requête la plus lente ?
Que donne `?tag=status:200` ?**

**Réponse :**
- **COUNT** : Candidat a traité **70 requêtes HTTP** au total.
- **MAX** : la requête la plus lente a duré **0,074 s (74 ms)**. Avec TOTAL_TIME = 1,069 s, la moyenne est
  d'environ 15 ms par requête.
- **`?tag=status:200`** : le filtre ne garde que les réponses réussies (HTTP 200). COUNT passe à **57** : les
  **13 requêtes** manquantes sont des erreurs **404** (`outcome=CLIENT_ERROR`, appels à une URL inexistante).
  Le MAX reste à 0,074 s, car la requête la plus lente était une réponse 200.
- Les tags disponibles (`uri`, `method`, `status`, `outcome`, `exception`) se combinent, par exemple
  `?tag=uri:/mic1/candidates&tag=method:GET`.
- Remarque : MAX est calculé sur une fenêtre glissante (≈ 2 min) et redescend à 0 sans nouvelles requêtes.

![Metrics](docs/metrics.png)
![Metrics status 200](docs/metrics-status200.png)

### 5. HealthIndicator personnalisé et mode maintenance (Candidat)

En fonctionnement normal, `/actuator/health` affiche notre composant personnalisé :

```json
"candidat": { "status": "UP", "details": { "maintenance": false, "candidates": 1 } }
```

Activation de la maintenance :

```powershell
Invoke-RestMethod -Method Put "http://localhost:8081/api/maintenance?enabled=true"
```

- `/actuator/health` passe **OUT_OF_SERVICE** (HTTP 503) :
  ```json
  "candidat": { "status": "OUT_OF_SERVICE",
                "details": { "maintenance": true, "message": "Service retiré volontairement (maintenance)" } }
  ```
- Le client Eureka de Spring Cloud convertit OUT_OF_SERVICE en **DOWN** : **CANDIDAT apparaît DOWN** dans le dashboard.
- Avec `enabled=false`, Candidat repasse **UP** (dans Eureka après ≈ 30 s, au prochain envoi de statut).

![Candidat maintenance](docs/candidat-maintenance-health.png)
![Eureka : CANDIDAT DOWN](docs/eureka-candidat-down.png)

### 6. `/actuator/loggers` – niveau des logs modifié sans redémarrage

```powershell
Invoke-RestMethod -Method Post "http://localhost:8081/actuator/loggers/com.awd.candidat" -ContentType "application/json" -Body '{"configuredLevel":"DEBUG"}'
Invoke-RestMethod "http://localhost:8081/actuator/loggers/com.awd.candidat"
```

```
configuredLevel effectiveLevel
--------------- --------------
DEBUG           DEBUG
```

![Loggers DEBUG](docs/loggers-debug.png)

### 7. Partie 5 – Santé de Meeting (Node.js) et Notification (Python)

| Service | `/health` | `/info` | Déclaration Eureka |
|---|---|---|---|
| Meeting | `{"status":"UP"}` | nom + version lus dans `package.json` | `healthCheckUrl`, `statusPageUrl` (eureka-js-client) |
| Notification | `{"status":"UP"}` | nom + version de l'application FastAPI | `health_check_url`, `status_page_url` (py-eureka-client) |

Vérification dans le registre `GET http://localhost:8761/eureka/apps/NOTIFICATION` (2 instances) :

```xml
<instanceId>localhost:notification:8084</instanceId>
<status>UP</status>
<statusPageUrl>http://localhost:8084/info</statusPageUrl>
<healthCheckUrl>http://localhost:8084/health</healthCheckUrl>
<leaseInfo>
  <renewalIntervalInSecs>30</renewalIntervalInSecs>
  <durationInSecs>90</durationInSecs>
</leaseInfo>
```

- `renewalIntervalInSecs = 30` : heartbeat toutes les 30 secondes.
- `durationInSecs = 90` : l'instance est retirée du registre après 90 secondes sans heartbeat.

![Registre NOTIFICATION](docs/eureka-notification-registry.png)

---

## ✅ Tests automatisés

| Service | Commande | Résultat |
|---|---|---|
| meeting | `npm test` | 7 tests réussis |
| notification | `pytest` | 7 tests réussis |
| eureka-server, candidat, job, candidature | `mvn test` | Eureka désactivé pendant les tests |

---

## 📌 Conclusion

- **Eureka** sait **quels services existent et où** ils se trouvent : les services Java, Node.js et Python
  s'enregistrent tous dans le même annuaire, et plusieurs instances d'un même service sont gérées.
- **Actuator** sait **si chaque service va bien** : santé détaillée, informations, métriques et niveau des logs.
- Reliés ensemble (`eureka.client.healthcheck.enabled=true`), une instance en panne passe automatiquement **DOWN**
  dans Eureka et n'est plus proposée aux autres services.