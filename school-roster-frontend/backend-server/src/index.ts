// src/index.ts

import express, { Request, Response, NextFunction } from 'express';
import fetch from 'node-fetch';
import dotenv from 'dotenv';

dotenv.config();
const app = express();
const PORT = process.env.PORT || 3001;

app.use(express.json());

// === Current weather proxy ===
app.get(
  '/api/weather',
  async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const q = String(req.query.q || '');
      const units = String(req.query.units || 'metric');
      const key = process.env.OPENWEATHERMAP_KEY;
      if (!key) {
        res.status(500).json({ error: 'API key not configured' });
        return;
      }

      const url =
        `https://api.openweathermap.org/data/2.5/weather` +
        `?q=${encodeURIComponent(q)}` +
        `&units=${encodeURIComponent(units)}` +
        `&appid=${encodeURIComponent(key)}`;

      const apiRes = await fetch(url);
      if (!apiRes.ok) {
        const errText = await apiRes.text();
        res.status(apiRes.status).send(errText);
        return;
      }

      const data = await apiRes.json();
      res.json(data);
      return;
    } catch (err) {
      next(err);
    }
  }
);

// === 7‑day forecast proxy ===
app.get(
  '/api/weather/forecast',
  async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const lat = String(req.query.lat || '');
      const lon = String(req.query.lon || '');
      const units = String(req.query.units || 'metric');
      const key = process.env.OPENWEATHERMAP_KEY;
      if (!key) {
        res.status(500).json({ error: 'API key not configured' });
        return;
      }
      if (!lat || !lon) {
        res.status(400).json({ error: 'Missing lat/lon parameters' });
        return;
      }

      const url =
        `https://api.openweathermap.org/data/3.0/onecall` +
        `?lat=${encodeURIComponent(lat)}` +
        `&lon=${encodeURIComponent(lon)}` +
        `&units=${encodeURIComponent(units)}` +
        `&appid=${encodeURIComponent(key)}`;

      const apiRes = await fetch(url);
      if (!apiRes.ok) {
        const errText = await apiRes.text();
        res.status(apiRes.status).send(errText);
        return;
      }

      const data = await apiRes.json();
      res.json(data);
      return;
    } catch (err) {
      next(err);
    }
  }
);

app.listen(PORT, () => {
  console.log(`🌤️ Weather proxy listening on http://localhost:${PORT}`);
});
