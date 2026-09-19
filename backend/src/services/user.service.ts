/**
 * spec: user storage keyed by google `sub`.
 * in-memory for now (no db wired up yet); swap the Maps for a db without
 * changing the exported function signatures.
 */
import { randomUUID } from 'node:crypto';
import type { TokenPayload } from 'google-auth-library';
import type { User } from '../types/user';

const usersByGoogleId = new Map<string, User>();
const usersById = new Map<string, User>();

/** returns the existing user for this google account, or creates one. */
export function findOrCreateUser(profile: TokenPayload): {
  user: User;
  isNewUser: boolean;
} {
  const existing = usersByGoogleId.get(profile.sub);
  if (existing) {
    return { user: existing, isNewUser: false };
  }

  const user: User = {
    id: randomUUID(), // concern: technically possible that two users happen to be assigned the same ID?
    googleId: profile.sub,
    email: profile.email ?? '',
    firstName: profile.given_name ?? '',
    lastName: profile.family_name ?? '',
    pictureUrl: profile.picture,
    createdAt: new Date(),
  };
  usersByGoogleId.set(user.googleId, user);
  usersById.set(user.id, user);
  return { user, isNewUser: true };
}

export function findUserById(id: string): User | undefined {
  return usersById.get(id);
}
