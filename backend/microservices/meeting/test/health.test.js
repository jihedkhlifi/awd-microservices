// Atelier Observabilité : routes /health et /info, configuration Eureka
const { test, before, after } = require('node:test');
const assert = require('node:assert/strict');
const app = require('../src/app');
const { createEurekaClient } = require('../src/config/eureka');
const pkg = require('../package.json');

let server;
let baseUrl;

before(async () => {
  server = app.listen(0);
  await new Promise((resolve) => server.once('listening', resolve));
  baseUrl = `http://localhost:${server.address().port}`;
});

after(() => server.close());

test('GET /health returns {"status":"UP"} with 200', async () => {
  const res = await fetch(`${baseUrl}/health`);
  assert.equal(res.status, 200);
  assert.deepEqual(await res.json(), { status: 'UP' });
});

test('GET /info returns name and version from package.json', async () => {
  const res = await fetch(`${baseUrl}/info`);
  assert.equal(res.status, 200);
  const body = await res.json();
  assert.equal(body.app.name, pkg.name);
  assert.equal(body.app.version, pkg.version);
});

test('Eureka instance is MEETING with health and status URLs on its own port', () => {
  const client = createEurekaClient(9093);
  const { instance, eureka } = client.config;
  assert.equal(instance.app, 'MEETING');
  assert.equal(instance.port.$, 9093);
  assert.equal(instance.instanceId, 'localhost:meeting:9093');
  assert.equal(instance.healthCheckUrl, 'http://localhost:9093/health');
  assert.equal(instance.statusPageUrl, 'http://localhost:9093/info');
  assert.equal(eureka.servicePath, '/eureka/apps/');
});
