/**
 * spec: issues and verifies short-lived session tokens (HS256 JWTs).
 * the token's `sub` claim is our internal user id.
 */
import jwt from 'jsonwebtoken';
import { env } from '../config/env';

export function issueSessionToken(userId: string): string {
  return jwt.sign({}, env.jwtSecret, {
    subject: userId,
    expiresIn: env.sessionTtlSeconds,
    algorithm: 'HS256',
  });
}

/** returns the user id, or throws if the token is invalid/expired. */
export function verifySessionToken(token: string): string {
  // pin the algorithm so a forged "alg: none" token is rejected
  const decoded = jwt.verify(token, env.jwtSecret, { algorithms: ['HS256'] });
  if (typeof decoded === 'string' || typeof decoded.sub !== 'string') {
    throw new Error('Malformed session token');
  }
  return decoded.sub;
}
