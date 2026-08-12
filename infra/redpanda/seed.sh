#!/bin/bash
set -euo pipefail

BROKERS="${REDPANDA_BROKERS:-redpanda:9092}"
TOPIC_NAME="${TRANSACTIONS_TOPIC:-transacoes-financeiras-processadas}"

echo "Waiting for Redpanda broker at ${BROKERS}..."
until rpk cluster info --brokers "${BROKERS}" >/dev/null 2>&1; do
  echo "  not ready yet, retrying in 2s..."
  sleep 2
done
echo "Redpanda broker is ready."

if rpk topic describe "${TOPIC_NAME}" --brokers "${BROKERS}" >/dev/null 2>&1; then
  echo "Topic '${TOPIC_NAME}' already exists, skipping creation."
else
  echo "Creating topic '${TOPIC_NAME}'..."
  rpk topic create "${TOPIC_NAME}" --brokers "${BROKERS}" --partitions 1 --replicas 1
fi

echo "Topic '${TOPIC_NAME}' is ready (no seed messages — use 'make kafka-produce-transactions-events' to generate test data)."