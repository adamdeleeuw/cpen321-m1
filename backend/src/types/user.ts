/** spec: user record shapes shared by services and controllers. */

export interface User {
  id: string; // internal id (uuid)
  googleId: string; // google `sub`; stable key, unlike email
  email: string;
  firstName: string;
  lastName: string;
  pictureUrl?: string | undefined;
  createdAt: Date;
}

// what we send to the client (no googleId)
export interface PublicUser {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  pictureUrl?: string | undefined;
}

export function toPublicUser(user: User): PublicUser {
  return {
    id: user.id,
    email: user.email,
    firstName: user.firstName,
    lastName: user.lastName,
    pictureUrl: user.pictureUrl,
  };
}
