import { type Request, type Response } from 'express';

const OK = 200;

export function getMyName(_req: Request, res: Response) {
    res.status(OK).json({
        firstName: 'Adam',
        lastName: 'de Leeuw'
    });
}