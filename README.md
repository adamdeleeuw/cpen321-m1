# Adam's Implementation of M1

_Keep this README up to date with the steps required to build and run the frontend and backend (including any scripts, config files, and environment variables). TAs ill follow these instructions._

## Requirements

Install the following before the frontend or backend setup steps:

- [git](https://git-scm.com/install/)


--- 

## Frontend Setup

### Requirements

- [Android Studio](https://developer.android.com/studio) (latest version)
- [Java 17](https://adoptium.net/temurin/releases/?version=17)
- [Android SDK](https://developer.android.com/studio#command-tools) with API level 36+ (Android 16)

### Setup

1. **Open project**: Open the `frontend/` directory in Android Studio
2. **Sync Gradle**: Android Studio will automatically prompt you to sync the project. Click "Sync Now". You can also manually run `cd frontend && ./gradlew build` to trigger the sync and download the necessary dependencies.
3. **Configure Android SDK**: Ensure you have Android SDK 36 installed.
4. **Set up emulator/device**:
   - Create a new AVD (Android Virtual Device) by selecting Pixel 9 as the device and Android Baklava (API level 36) as the system image.
   - Alternatively, connect a physical Android device running Android 16 (API level 36).
5. **Setup app config**: Copy the example file, then fill in local values:
   ```bash
   cp frontend/local.properties.example frontend/local.properties
   ```
   Set at least:
   - `sdk.dir`: path to your Android SDK. Android Studio usually writes this the first time you open `frontend/`. On Mac it is often `sdk.dir=/Users/<username>/Library/Android/sdk`.
   - `API_BASE_URL`: backend URL baked into the APK. Use `https://136-67-54-50.sslip.io` for the deployed backend on the GCP VM. For a backend on your own machine, use `http://10.0.2.2:3000` on the emulator (`10.0.2.2` is the host machine; plain HTTP is only allowed to it in debug builds).


### Build and Run

- **Debug build**: Click the green play button in the toolbar, to compile the code, package a debug APK, and install it on the connected device or running emulator. Alternatively, from the project root, run `./scripts/run-frontend.sh`.
- **Release build**: Go to Build -> Generate Signed App Bundle or APK -> APK. Follow the on-screen instructions to create a key, and select the "release" build variant. You will then have to manually install the generated APK on your device or the running emulator.


### Backend Configuration

Ensure the backend server is running and update the base URL in the app configuration if needed.

---
## Backend Setup

You can run the backend in one of two ways:
* Locally via Node.js 
* Via Docker Compose

Both ways use the same `backend/.env` file (see below).

### Environment configuration

From the project root:

```bash
cp backend/.env.example backend/.env
```

Set at least:
- `GOOGLE_BACKEND_CLIENT_ID`: the web/backend OAuth client ID the Android app requests ID tokens for.
- `JWT_SECRET`: a long random string used to sign auth tokens.
- `SERVER_PUBLIC_IP`: the IP shown on the connection info screen (`136.67.54.50` on the VM).
- `PORT` (optional): defaults to `3000` if unset.

When running locally with Node, `backend/.env.dev` is read first, then `backend/.env`.


### Option 1: Run locally

**Requirements:** 
- [Node.js](https://nodejs.org/en/download/) 22+
- [npm](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm) 10+

**Setup:** 
1. Install dependencies:

   ```bash
   cd backend
   npm install
   ```

2. **Development** (TypeScript with auto-reload):

   ```bash
   npm run dev
   ```

3. **Production build** (optional):

   ```bash
   npm run build
   npm start
   ```

### Option 2: Run with Docker Compose

**Requirements:** 
- [Docker](https://docs.docker.com/desktop/setup/install) and [Docker Compose](https://docs.docker.com/desktop/setup/install) v2.24+
- [curl](https://curl.se/download.html)

**Setup**
1. **Start** (from the project root):

   ```bash
   ./scripts/run-backend.sh
   ```

   Or run Compose directly:

   ```bash
   docker compose up --build -d
   ```

2. **Stop**:

   ```bash
   docker compose down
   ```

## Deployment (GCP VM)

The backend runs on the VM `cpen321-backend-vm-adam` (static IP `136.67.54.50`) and is served at **https://136-67-54-50.sslip.io**.

- `docker-compose.prod.yml` adds [Caddy](https://caddyserver.com) in front of the backend. Caddy gets a Let's Encrypt certificate automatically and proxies HTTP and the `/ws/pixels` WebSocket to the backend, which is not exposed on its own.
- Every push to `main` that touches `backend/`, `deploy/`, the compose files or the deploy script runs `.github/workflows/backend.yml`. That workflow typechecks, tests and builds, then SSHes into the VM and runs `scripts/deploy-backend.sh`.
- To deploy by hand: `gcloud compute ssh --zone us-west1-a cpen321-backend-vm-adam --project cpen321-m1-508304`, then `bash ~/cpen321-m1/scripts/deploy-backend.sh`.
- Secrets live only in `~/cpen321-m1/backend/.env` on the VM, never in git or GitHub.

## Additional Setup

_Please specify any other additional setup steps non-specific to either frontend nor backend_