#!/bin/sh
host="$1"
shift
until nc -z "$host" 6379; do
  echo "Waiting for Redis at $host:6379..."
  sleep 1
done
exec "$@"