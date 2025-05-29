// src/routes/configRoutes.ts

import { Router } from 'express';
import { getGooglePlacesKey } from '../controllers/configController';

const router = Router();

router.get('/google-places-key', getGooglePlacesKey);

export default router;
