/**
 * spec: loads env vars (.env.dev, then .env) and exposes validated config.
 * auth secrets are getters so importing the app (e.g. in tests) doesn't throw;
 * call assertAuthEnv() at startup to fail fast.
 */
import dotenv from 'dotenv';

// first file wins per variable; real env vars (e.g. from docker compose) win over both
dotenv.config({ path: ['.env.dev', '.env'], quiet: true });

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
  // course-provided pixel stream; overridable so tests can point at a local fake
  pixelServerUrl: process.env.PIXEL_SERVER_URL ?? 'wss://8.229.22.124',
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
