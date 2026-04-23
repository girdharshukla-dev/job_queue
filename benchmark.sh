#!/bin/bash

URL="http://localhost:8080/jobs"
TOTAL_JOBS=200
CONCURRENCY=50

echo "Submitting $TOTAL_JOBS jobs with concurrency $CONCURRENCY..."

start_time=$(date +%s)

submit_job() {
  curl -s -X POST "$URL" \
    -H "Content-Type: application/json" \
    -d '{
      "type": "image.process",
      "payload": { "imageId": 42 },
      "maxAttempts": 1
    }' > /dev/null
}

running=0

for ((i=1; i<=TOTAL_JOBS; i++)); do
  submit_job &

  ((running++))

  if (( running >= CONCURRENCY )); then
    wait
    running=0
  fi
done

wait

echo "All jobs submitted."

# ---- WAIT FOR PROCESSING ----
# Your job sleeps ~1 sec → 8 workers → ~8 jobs/sec
# crude but enough

echo "Waiting for processing to finish..."
sleep 30

end_time=$(date +%s)

duration=$((end_time - start_time))
throughput=$((TOTAL_JOBS / duration))

echo "---------------------------------"
echo "Total time: $duration sec"
echo "Approx throughput: $throughput jobs/sec"
echo "Workers: 8"
echo "---------------------------------"#!/bin/bash

URL="http://localhost:8080/jobs"
TOTAL_JOBS=200
CONCURRENCY=50

echo "Submitting $TOTAL_JOBS jobs with concurrency $CONCURRENCY..."

start_time=$(date +%s)

submit_job() {
  curl -s -X POST "$URL" \
    -H "Content-Type: application/json" \
    -d '{
      "type": "image.process",
      "payload": { "imageId": 42 },
      "maxAttempts": 1
    }' > /dev/null
}

running=0

for ((i=1; i<=TOTAL_JOBS; i++)); do
  submit_job &

  ((running++))

  if (( running >= CONCURRENCY )); then
    wait
    running=0
  fi
done

wait

echo "All jobs submitted."


echo "Waiting for processing to finish..."
sleep 30

end_time=$(date +%s)

duration=$((end_time - start_time))
throughput=$((TOTAL_JOBS / duration))

echo "---------------------------------"
echo "Total time: $duration sec"
echo "Approx throughput: $throughput jobs/sec"
echo "Workers: 8"
echo "---------------------------------"
