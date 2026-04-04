For secrets export these in your shell
- DB_URL 
- DB_USER
- DB_PASSWORD
- REDIS_URL

Example assuming you spin up the docker compose:
```
export DB_URL=jdbc:postgresql://localhost:5432/jobqueue
export DB_USER=jobuser
export DB_PASSWORD=jobpass
export REDIS_URL=redis://localhost:6379
```