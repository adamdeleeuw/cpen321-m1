/**
 * spec: one connection to the course pixel server per call. each frame is
 * passed to onPixel exactly as received; onClose fires once when the
 * connection ends (either side). knows nothing about our own clients.
 */
import WebSocket from 'ws';
import { env } from '../config/env';

/** returns a function that closes the upstream connection. */
export function openPixelStream(
  onPixel: (raw: string) => void,
  onClose: () => void
): () => void {
  const upstream = new WebSocket(env.pixelServerUrl);
  let closed = false;

  const finish = (): void => {
    if (closed) return;
    closed = true;
    onClose();
  };

  upstream.on('message', (data) => {
    if (!closed) onPixel(data.toString());
  });
  // an unhandled 'error' event would crash the process; 'close' always follows
  upstream.on('error', (err) => console.error(`pixel server: ${err.message}`));
  upstream.on('close', finish);

  return () => {
    closed = true; // caller initiated: don't call back into it
    upstream.terminate();
  };
}
