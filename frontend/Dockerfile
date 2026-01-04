# Stage 1: Build the Angular application
FROM node:24-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# 'production' matches the configuration in your angular.json
RUN npm run build -- --configuration production

# Stage 2: Serve with Nginx
FROM nginx:alpine
# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*
# Copy static assets from builder stage
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Copy our custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
