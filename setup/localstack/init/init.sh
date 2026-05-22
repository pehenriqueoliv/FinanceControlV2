#!/bin/bash

DEFAULT_REGION="us-east-1"

buckets=(
  "bucket_example"
)

queues=(
  "queue_example"
)

# shellcheck disable=SC2048
for i in ${buckets[*]}; do
  awslocal s3 mb s3://${i}
  awslocal s3api put-bucket-acl --bucket ${i} --acl public-read
done

if [ -d "/s3-files" ]; then
  for dir in "/s3-files"/*/; do
    bucket_name=$(basename "$dir")
    echo "Sincronizando '$dir' com o bucket S3 '$bucket_name'..."
    awslocal s3 sync "$dir" "s3://$bucket_name/"
  done
fi

# shellcheck disable=SC2048
for i in ${queues[*]}; do
  awslocal sqs create-queue --region ${DEFAULT_REGION} --queue-name "${i}_DLQ"
  awslocal sqs create-queue --region ${DEFAULT_REGION} --queue-name "${i}" \
    --attributes "{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"arn:aws:sqs:${DEFAULT_REGION}:000000000000:${i}_DLQ\\\",\\\"maxReceiveCount\\\":\\\"3\\\"}\"}"
done

if [ -f "/secrets/structure_credentials" ]; then
  read structure_CLIENT_ID < <(sed -n '1p' "/secrets/structure_credentials")
  read structure_CLIENT_SECRET < <(sed -n '2p' "/secrets/structure_credentials")
else
  structure_CLIENT_ID="client_FINANCIAL-LAB"
  structure_CLIENT_SECRET="kMc0bIbrS6B5RxOg6RLIPZwARR1Cz8H3"
fi

awslocal secretsmanager create-secret \
    --region "${DEFAULT_REGION}" \
    --name "financial_lab/structure_credentials" \
    --description "Auth credentials structure" \
    --secret-string '{"client_id":"'"$structure_CLIENT_ID"'","client_secret":"'"$structure_CLIENT_SECRET"'"}'