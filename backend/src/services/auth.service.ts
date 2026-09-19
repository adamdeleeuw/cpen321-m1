/**
 * spec: google sign-in logic.
 * - verifyGoogleIdToken: asks google to validate an id token (signature,
 *   expiry, issuer, audience == our client id) and returns its payload.
 * - authenticateWithGoogle: verify token -> find/create user -> issue session.
 * note: OAuth2Client is google's client for both oauth2 and OIDC; we only use
 * it for OIDC id-token verification.
 */
import { OAuth2Client, type TokenPayload } from 'google-auth-library';
import { env } from '../config/env';
import { findOrCreateUser } from './user.service';
import { issueSessionToken } from './session.service';
import type { User } from '../types/user';

// created lazily so a missing env var doesn't crash on import
let client: OAuth2Client | undefined;

export async function verifyGoogleIdToken(token: string): Promise<TokenPayload> {
  client ??= new OAuth2Client(env.googleClientId);

  const ticket = await client.verifyIdToken({
    idToken: token,
    audience: env.googleClientId,
  });

  const payload = ticket.getPayload();
  if (!payload) {
    throw new Error('Google token does not have a payload');
  }
  return payload;
}

export interface AuthResult {
  sessionToken: string;
  expiresIn: number; // seconds
  isNewUser: boolean;
  user: User;
}

/** throws if the google token is invalid. */
export async function authenticateWithGoogle(idToken: string): Promise<AuthResult> {
  const payload = await verifyGoogleIdToken(idToken);
  const { user, isNewUser } = findOrCreateUser(payload);

  return {
    sessionToken: issueSessionToken(user.id),
    expiresIn: env.sessionTtlSeconds,
    isNewUser,
    user,
  };
}
