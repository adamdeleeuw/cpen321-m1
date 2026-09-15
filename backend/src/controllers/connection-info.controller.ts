import {type Request, type Response} from 'express';
import {getPublicServerIp} from '../services/connection-info.service';
import {getLocalServerTime} from '../services/connection-info.service';
import {getPublicClientIP} from '../services/connection-info.service';

const OK = 200;

export function getConnectionInfo(req: Request, res: Response) {
    // parse GET request to get the clients time
    // use 'UTC' as fallback in case the req.timZone object is undefined or has an unexpected type
    const clientTimeZone = typeof req.query.timeZone === 'string' ? req.query.timeZone : 'UTC'

    if (!getPublicServerIp()) throw new Error('');
    if (!getPublicClientIP(req)) throw new Error('');

    res.status(OK).json({
        serverIp: getPublicServerIp(), // should add a check?
        serverTime: getLocalServerTime(),
        clientIp: getPublicClientIP(req)
    });
}