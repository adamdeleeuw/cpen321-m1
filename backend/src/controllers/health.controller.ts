import {type Request, type Response} from 'express';

export function getHealth(_req: Request, res: Response): void {
    res.json({status: 'ok'});
}