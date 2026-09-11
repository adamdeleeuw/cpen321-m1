import {type Request, type Response} from 'express';

export function getHealth(_req: Readonly<Request>, res: Response): void {
    res.json({status: 'ok'});
}