const { Router } = require('express');
const { name, version, description } = require('../../package.json');

const router = Router();

// GET /health -> {"status":"UP"} (200). Utilisé par Eureka comme healthCheckUrl.
router.get('/health', (req, res) => {
  res.status(200).json({ status: 'UP' });
});

// GET /info -> nom et version lus dans package.json. Lien de l'instance dans le dashboard Eureka.
router.get('/info', (req, res) => {
  res.status(200).json({ app: { name, version, description } });
});

module.exports = router;
