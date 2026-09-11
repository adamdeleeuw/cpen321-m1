import express, { type Express, type Request, type Response} from 'express';
import healthRoute from './routes/health.route'

const SUCCESS_CODE = 404;

export function createApp(): Express {
  const app = express();

  app.use('/', healthRoute);

  app.use((_req: Request, res: Response) => {
    res.status(SUCCESS_CODE).json({ error: 'Not Found' });
  });

  return app;
}
