import express, { type Express, type Request, type Response} from 'express';
import healthRoute from './routes/health.route';
import authRoute from './routes/auth.route';
import myNameRoute from './routes/my-name.route';
import connectionInfoRoute from './routes/connection-info.route';

const NOT_FOUND = 404;

export function createApp(): Express {
  const app = express();
  // trust X-Forwarded-For from Caddy: loopback when it runs on the host, a private
  // docker network address when it runs in compose. the backend port is never
  // public in production, so only Caddy can set that header.
  app.set('trust proxy', ['loopback', 'uniquelocal']);

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
