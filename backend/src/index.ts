import { createApp } from './app';
import { env } from './config/env';

const SUCCESS_CODE = 0;

const app = createApp();

const server = app.listen(env.port, () => {
  console.log(`Server listening on port ${env.port}`);
});

for (const signal of ['SIGINT', 'SIGTERM'] as const) {
  process.on(signal, () => {
    server.close(() => {
      process.exit(SUCCESS_CODE);
    });
  });
}
