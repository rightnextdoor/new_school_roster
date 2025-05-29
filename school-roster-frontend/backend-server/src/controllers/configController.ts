// src/controllers/configController.ts

import { Request, Response, NextFunction } from 'express';

/**
 * Controller for serving configuration values to the front-end.
 */
export const getGooglePlacesKey = (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const key = process.env.GOOGLE_PLACES_API_KEY;
    if (!key) {
      return res
        .status(500)
        .json({ error: 'Google Places API key not configured' });
    }
    // If you have authentication middleware, ensure only authorized users can hit this.
    return res.json({ key });
  } catch (err) {
    next(err);
  }
};
