import express, { type Express, type Request, type Response} from 'express';
import healthRoute from './routes/health.route';
import authRoute from './routes/auth.route';
import myNameRoute from './routes/my-name.route';
import connectionInfoRoute from './routes/connection-info.route';

const NOT_FOUND = 404;

export function createApp(): Express {
  const app = express();
  app.set('trust proxy', 'loopback'); // trust 127.0.0.1/::1 (where Caddy proxy sits)

  // middleware: allows for defined req.body access
  app.use(express.json());

  app.use('/health', healthRoute);
  app.use('/api/auth', authRoute);
  app.use('/api/my-name', myNameRoute);
  app.use('/api/connection-info', connectionInfoRoute);

  app.use((_req: Request, res: Response) => {
    res.status(NOT_FOUND).json({ error: 'Not Found' });
  });

  return app;
}
