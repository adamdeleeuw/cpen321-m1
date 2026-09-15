/**
 * This service provides APIs for:
 * - Server public IP
 * - Server local time
 */
import { type Request } from 'express';

export function getPublicServerIp(): string | undefined {
    return process.env.SERVER_PUBLIC_IP;
}

export function getPublicClientIP(req: Request): string | undefined {
    return req.ip;
}

function formatTimeFromElements(timeElements: Intl.DateTimeFormatPart[]): string {
    // get the element based on type
    // if element or element value are undefined use ''
    const getElement = (type: string) => {
        return timeElements.find((element) => element.type === type)?.value ?? '';
    }

    const localTime = `${getElement('hour')}:${getElement('minute')}:${getElement('second')}`;
    const offset = getElement('timeZoneName');
    const gmtOffset = offset === 'GMT' ? 'GMT+00:00' : offset;

    return `${localTime} ${gmtOffset}`;
}

export function getLocalServerTime(): string {
    const timeElements = new Intl.DateTimeFormat('en-CA', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        timeZoneName: 'longOffset'
    }).formatToParts(new Date());

    // Note: DateTimeFormat() gets server timezone from the machine its deployed on
    return formatTimeFromElements(timeElements);
}