const app = require('./app');
const { createEurekaClient } = require('./config/eureka');

const PORT = process.env.PORT || 8083;

const server = app.listen(PORT, () => {
  console.log(`meeting microservice running on http://localhost:${PORT}`);
  console.log(`Swagger UI: http://localhost:${PORT}/swagger-ui`);
});

// ---- Enregistrement dans Eureka (désactivable avec EUREKA_ENABLED=false) ----
let eurekaClient = null;
if (process.env.EUREKA_ENABLED !== 'false') {
  eurekaClient = createEurekaClient(PORT);
  eurekaClient.logger.level('warn');
  eurekaClient.start((error) => {
    if (error) {
      console.error('Eureka registration failed:', error.message || error);
    } else {
      console.log(`registered in Eureka as MEETING (instance localhost:meeting:${PORT})`);
    }
  });
}

// ---- Arrêt propre : désinscription d'Eureka puis fermeture du serveur HTTP ----
function shutdown() {
  const close = () => server.close(() => process.exit(0));
  if (eurekaClient) {
    eurekaClient.stop(close);
  } else {
    close();
  }
  setTimeout(() => process.exit(0), 5000).unref();
}
process.on('SIGINT', shutdown);
process.on('SIGTERM', shutdown);
