/**
 * spec: guards non-auth routes. expects `Authorization: Bearer <sessionToken>`.
 * on success stores the user id in res.locals.userId; otherwise responds 401.
 */
import { type NextFunction, type Request, type Response } from 'express';
import { verifySessionToken } from '../services/session.service';
import { findUserById } from '../services/user.service';

const UNAUTHORIZED = 401;
const BEARER_PREFIX = 'Bearer ';

export function requireAuth(req: Request, res: Response, next: NextFunction): void {
  const header = req.headers.authorization;

  if (!header?.startsWith(BEARER_PREFIX)) {
    res.status(UNAUTHORIZED).json({ error: 'Missing session token' });
    return;
  }

  try {
    const userId = verifySessionToken(header.slice(BEARER_PREFIX.length));
    // user store is in-memory: a server restart invalidates old sessions
    if (!findUserById(userId)) {
      res.status(UNAUTHORIZED).json({ error: 'Unknown user' });
      return;
    }
    res.locals.userId = userId;
    next();
  } catch {
    res.status(UNAUTHORIZED).json({ error: 'Invalid or expired session token' });
  }
}
