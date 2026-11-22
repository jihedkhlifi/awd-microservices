/**
 * Enregistrement du microservice Meeting dans le serveur Eureka (atelier Eureka, partie 3 – tâche 1)
 * + déclaration des URL de santé (atelier Observabilité, partie 5 – tâche 1).
 *
 * Variables d'environnement :
 *   PORT            port de l'instance (8083 par défaut) -> permet de lancer plusieurs instances
 *   HOSTNAME_EUREKA nom d'hôte publié dans Eureka (localhost par défaut)
 *   EUREKA_HOST     hôte du serveur Eureka (localhost par défaut)
 *   EUREKA_PORT     port du serveur Eureka (8761 par défaut)
 */
const { Eureka } = require('eureka-js-client');

function createEurekaClient(port) {
  const host = process.env.HOSTNAME_EUREKA || 'localhost';
  const baseUrl = `http://${host}:${port}`;

  return new Eureka({
    instance: {
      app: 'MEETING',
      instanceId: `${host}:meeting:${port}`, // unique par instance (même machine, ports différents)
      hostName: host,
      ipAddr: '127.0.0.1',
      port: { $: Number(port), '@enabled': 'true' },
      vipAddress: 'meeting',
      secureVipAddress: 'meeting',
      homePageUrl: `${baseUrl}/`,
      statusPageUrl: `${baseUrl}/info`, // lien de l'instance dans le dashboard Eureka
      healthCheckUrl: `${baseUrl}/health`,
      dataCenterInfo: {
        '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
        name: 'MyOwn',
      },
    },
    eureka: {
      host: process.env.EUREKA_HOST || 'localhost',
      port: Number(process.env.EUREKA_PORT || 8761),
      servicePath: '/eureka/apps/', // chemin de l'API REST d'un serveur Spring Cloud Eureka
      maxRetries: 10,
      requestRetryDelay: 2000,
    },
  });
}

module.exports = { createEurekaClient };
