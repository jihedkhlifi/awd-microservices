/**
 * OpenAPI 3 specification of the meeting microservice.
 * Served as JSON at /v3/api-docs and as Swagger UI at /swagger-ui.
 *
 * TODO (students): document every new route in `paths` and every new object in `components.schemas`.
 */
const PORT = process.env.PORT || 8083;

module.exports = {
  openapi: '3.0.3',
  info: {
    title: 'Meeting Microservice API',
    version: '1.0.0',
    description: 'Meeting microservice (Node.js / Express, no database). ' +
      'Only the hello endpoint is implemented; the meeting logic is to be developed by students.',
    contact: { name: 'Badia Abouhdid' },
  },
  servers: [{ url: `http://localhost:${PORT}`, description: 'Local' }],
  tags: [
    { name: 'Meetings', description: 'Meeting endpoints' },
    { name: 'Health', description: 'Santé et informations (déclarées dans Eureka)' },
  ],
  paths: {
    '/health': {
      get: {
        tags: ['Health'],
        summary: 'Santé du service (healthCheckUrl Eureka)',
        operationId: 'health',
        responses: { 200: { description: 'Service UP', content: { 'application/json': { example: { status: 'UP' } } } } },
      },
    },
    '/info': {
      get: {
        tags: ['Health'],
        summary: 'Nom et version du service (statusPageUrl Eureka)',
        operationId: 'info',
        responses: {
          200: {
            description: 'Informations du service',
            content: { 'application/json': { example: { app: { name: 'meeting', version: '1.0.0' } } } },
          },
        },
      },
    },
    '/api/meetings/hello': {
      get: {
        tags: ['Meetings'],
        summary: 'Hello from the meeting microservice',
        description: 'Checks that the microservice is up and returns a greeting message.',
        operationId: 'hello',
        responses: {
          200: {
            description: 'Greeting message',
            content: {
              'application/json': {
                schema: { $ref: '#/components/schemas/HelloResponse' },
                example: { message: "hello I'm microservice meeting" },
              },
            },
          },
        },
      },
    },

    // TODO (students): document the CRUD routes, for example:
    // '/api/meetings': { get: {...}, post: {...} },
    // '/api/meetings/{id}': { get: {...}, put: {...}, delete: {...} },
  },
  components: {
    schemas: {
      HelloResponse: {
        type: 'object',
        properties: {
          message: { type: 'string', example: "hello I'm microservice meeting" },
        },
        required: ['message'],
      },

      // TODO (students): add the Meeting schema, for example:
      // Meeting: {
      //   type: 'object',
      //   properties: {
      //     id: { type: 'integer', example: 1 },
      //     title: { type: 'string', example: 'Technical interview' },
      //     date: { type: 'string', format: 'date-time', example: '2026-10-05T10:00:00Z' },
      //     candidateId: { type: 'integer', example: 1 },
      //     jobId: { type: 'integer', example: 1 },
      //   },
      // },

      Error: {
        type: 'object',
        properties: {
          status: { type: 'integer', example: 404 },
          error: { type: 'string', example: 'Not Found' },
          message: { type: 'string', example: 'Meeting 42 not found' },
        },
      },
    },
  },
};
