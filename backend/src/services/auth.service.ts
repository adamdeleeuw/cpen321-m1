/**
 * Authenticates the client's ID Token
 * Note: Google uses OAuth2Client as the universal client for both
 *       OAuth2 (authorization) and OpenID Connect (OIDC) (authentication).
 *       This app uses it for the latter.
 */

import {OAuth2Client, TokenPayload} from 'google-auth-library';
import dotenv from 'dotenv';

dotenv.config({path: '.env.dev'});

const tryClientId = process.env.GOOGLE_BACKEND_CLIENT_ID;
if (!tryClientId) {
    throw new Error('GOOGLE_BACKEND_CLIENT_ID is not defined');
}

// Guaranteed string (aka guaranteed that a client id exists and TS is knows it)
const clientId: string = tryClientId;

// clientId checked so we know the client exists
// If the constructor fails JS throws an exception which stops the program (fair)
const client = new OAuth2Client(clientId);

export async function verifyGoogleIdToken(token: string): Promise<TokenPayload> {
    const ticket = await client.verifyIdToken({
        idToken: token,
        audience: clientId,
    });

    const payload = ticket.getPayload();
    if (!payload) {
        throw new Error('Google token does not have a payload');
    }

    return payload;
}

