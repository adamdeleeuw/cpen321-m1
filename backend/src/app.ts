import express, { type Express, type Request, type Response} from 'express';
import healthRoute from './routes/health.route';
import authRoute from './routes/auth.route';
import myNameRoute from './routes/my-name.route';
import connectionInfoRoute from './routes/connection-info.route';
import { requireAuth } from './middleware/require-auth.middleware';

const NOT_FOUND = 404;

export function createApp(): Express {
  const app = express();

  // middleware: allows for defined req.body access
  app.use(express.json());

  // publicly available routes (no auth needed)
  app.use('/health', healthRoute);
  app.use('/api/auth', authRoute);

  // everything below requires a valid session token
  app.use('/api/my-name', requireAuth, myNameRoute);
  app.use('/api/connection-info', requireAuth, connectionInfoRoute);

  app.use((_req: Request, res: Response) => {
    res.status(NOT_FOUND).json({ error: 'Not Found' });
  });

  return app;
}
