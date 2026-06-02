all: up

up:
	@cd setup && docker compose up -d

stop:
	@cd setup && docker compose stop

destroy:
	@cd setup && docker compose stop && docker compose rm -f

logs:
	@cd setup && docker compose logs -f

prettier:
	@mvn prettier:write

lint:
	@mvn checkstyle:check

get-secret:
	@cd setup && docker compose exec -T financecontrol-localstack bash -c 'awslocal secretsmanager get-secret-value --region "us-east-1" --secret-id "$(secret)"'

con-aws:
	@cd setup && docker compose exec -it financecontrol-localstack /bin/bash
