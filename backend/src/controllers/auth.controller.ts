import {type Request, type Response} from 'express';
import {verifyGoogleIdToken} from '../services/auth.service';

const BAD_REQUEST = 400;
const UNAUTHORIZED = 401;
const OK = 200;

export async function logIntoGoogle(req: Request, res: Response): Promise<void> {
    const token = req.body?.idToken; // ? so it returns undefined without crashing if no body

    // Check if the token was in the body
    if (typeof token !== 'string' || token.length === 0) {
        res.status(BAD_REQUEST).json({error: 'No idToken'});
        return;
    }

    try {
        const payload = await verifyGoogleIdToken(token);

        res.status(OK).json({
            firstName: payload.given_name,
            lastName: payload.family_name
        });
    } catch {
        res.status(UNAUTHORIZED).json({error: 'Google ID Token is invalid'})
    }
}