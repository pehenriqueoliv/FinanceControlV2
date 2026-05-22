all: up

up:
	@cd setup && docker-compose up -d

stop:
	@cd setup && docker-compose stop

destroy:
	@cd setup && docker-compose stop && docker-compose rm -f

logs:
	@cd setup && docker-compose logs -f

prettier:
	@mvn prettier:write

lint:
	@mvn checkstyle:check

get-secret:
	@cd setup && docker-compose exec -T financial-lab-localstack bash -c 'awslocal secretsmanager get-secret-value --region "us-east-1" --secret-id "$(secret)"'

download-bucket:
	@aws --region "us-east-1" --endpoint-url=http://localhost:5566 s3 cp s3://$(bucket) ./setup/localstack/s3-saidas/$(bucket) --recursive

con-aws:
	@cd setup && docker-compose exec -it financial-lab-localstack /bin/bash
