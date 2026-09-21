# syntax=docker/dockerfile:1

# build stage: compile TypeScript to dist/ (needs devDependencies)
FROM node:22-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# runtime stage: compiled JS + production dependencies only
FROM node:22-alpine
WORKDIR /app

ENV NODE_ENV=production
# sets process.env.PORT
ENV PORT=3000

COPY package.json package-lock.json ./
RUN npm ci --omit=dev && npm cache clean --force

COPY --from=build /app/dist ./dist

# documents that the container listens on port 3000
EXPOSE 3000

# run as the image's unprivileged user rather than root
USER node

CMD ["node", "dist/index.js"]
