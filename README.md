For secrets export these in your shell
- DB_URL 
- DB_USER
- DB_PASSWORD

Example assuming you spin up the docker compose:
```
export DB_URL=jdbc:postgresql://localhost:5432/job_queue
export DB_USER=jobuser
export DB_PASSWORD=jobpass
```