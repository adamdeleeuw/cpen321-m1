/**
 * spec: serves the pixel stream to app clients at PIXEL_STREAM_PATH. each client
 * gets its own course-server connection (server.socket), relayed to it
 * immediately and unchanged; when either side closes, the other is closed too.
 * attaches to the existing http server: Caddy terminates TLS in front of it.
 */
import { type Server } from 'node:http';
import { WebSocketServer } from 'ws';
import { openPixelStream } from './server.socket';

const PIXEL_STREAM_PATH = '/ws/pixels';

/** returns a function that closes every client connection. */
export function attachWebSocket(server: Server): () => void {
  const wss = new WebSocketServer({ server, path: PIXEL_STREAM_PATH });

  wss.on('connection', (client) => {
    // an unhandled 'error' event would crash the process; 'close' always follows
    client.on('error', (err) => console.error(`pixel client: ${err.message}`));

    const closeUpstream = openPixelStream(
      (pixel) => {
        if (client.readyState === client.OPEN) client.send(pixel);
      },
      () => client.close()
    );
    client.on('close', closeUpstream);
  });

  return () => {
    // open sockets would otherwise keep server.close() from ever finishing
    wss.clients.forEach((client) => client.terminate());
    wss.close();
  };
}
