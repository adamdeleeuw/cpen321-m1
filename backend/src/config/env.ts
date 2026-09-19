/**
 * spec: loads env vars (.env.dev, then .env) and exposes validated config.
 * auth secrets are getters so importing the app (e.g. in tests) doesn't throw;
 * call assertAuthEnv() at startup to fail fast.
 */
import dotenv from 'dotenv';

dotenv.config({ path: '.env.dev' });
dotenv.config(); // .env; never overrides values already set

const rawPort = process.env.PORT;
const port =
  rawPort === undefined || rawPort === ''
    ? 3000
    : Number.parseInt(rawPort, 10);

if (Number.isNaN(port) || port < 1 || port > 65535) {
  throw new Error(`Invalid PORT: ${rawPort}`);
}

function requireEnv(name: string): string {
  const value = process.env[name];
  if (!value) {
    throw new Error(`${name} is not defined`);
  }
  return value;
}

// fixed time to live
const SESSION_TTL_SECONDS = 30 * 60; // 30 min sessions (no refresh yet)

export const env = {
  port,
  sessionTtlSeconds: SESSION_TTL_SECONDS,
  // web/backend client id; must equal the server client id the android app requests tokens for
  get googleClientId(): string {
    return requireEnv('GOOGLE_BACKEND_CLIENT_ID');
  },
  get jwtSecret(): string {
    return requireEnv('JWT_SECRET');
  },
};

export function assertAuthEnv(): void {
  void env.googleClientId;
  void env.jwtSecret;
}
