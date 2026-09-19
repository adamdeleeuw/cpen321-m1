/**
 * spec: POST /api/auth/google
 * body { idToken } -> 200 { sessionToken, expiresIn, isNewUser, user }
 * 400 if idToken missing, 401 if google rejects it.
 */
import { type Request, type Response } from 'express';
import { authenticateWithGoogle } from '../services/auth.service';
import { toPublicUser } from '../types/user';

const OK = 200;
const BAD_REQUEST = 400;
const UNAUTHORIZED = 401;

export async function logIntoGoogle(req: Request, res: Response): Promise<void> {
  const idToken: unknown = req.body?.idToken; // ?. so a missing body doesn't crash

  if (typeof idToken !== 'string' || idToken.length === 0) {
    res.status(BAD_REQUEST).json({ error: 'No idToken' });
    return;
  }

  try {
    const result = await authenticateWithGoogle(idToken);
    res.status(OK).json({
      sessionToken: result.sessionToken,
      expiresIn: result.expiresIn,
      isNewUser: result.isNewUser,
      user: toPublicUser(result.user),
    });
  } catch {
    // don't leak why verification failed
    res.status(UNAUTHORIZED).json({ error: 'Invalid credentials' });
  }
}
