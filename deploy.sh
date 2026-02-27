#!/bin/bash

set -e

echo "=== Counter Project Deployment ==="
echo ""

# Stop and remove all containers
echo "Stopping and removing containers..."
podman-compose down -v 2>/dev/null || true

# Remove old images
echo "Removing old images..."
podman rmi counterproject-backend counterproject-frontend 2>/dev/null || true

# Build images without cache
echo "Building backend image (no cache)..."
podman build --no-cache -t counterproject-backend -f backend/Containerfile backend/

echo "Building frontend image (no cache)..."
podman build --no-cache -t counterproject-frontend -f frontend/Containerfile frontend/

# Start services
echo "Starting services..."
podman-compose up -d

echo ""
echo "=== Deployment Complete ==="
echo "Frontend: http://localhost"
echo "Backend:  http://localhost:8080/counterproject/api"
echo "Database: localhost:5432"
echo ""
echo "Waiting for services to be ready..."
sleep 5

echo "Checking service status..."
podman-compose ps

echo ""
echo "To view logs: podman-compose logs -f"
echo "To stop:      podman-compose down"
echo "To stop and remove volumes: podman-compose down -v"
