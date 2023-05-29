# Allure server postgres sample
* This is not Allure server. This aims to prototype integrating Postgres into Allure server
* Upload zip file: Store all content of allure-results files in Postgres
* Get allure-result: Retrieve allure-results data from Postgres, generate json files.

## Getting started

### Prerequisites
* Docker
* Java 11

### Installing
Start Postgres using docker-compose (or you can use Postgres instance installed in your machine)
```shell
docker compose -f ./src/main/docker/postgres.yml up -d
```
Run or debug the project
```shell
./gradlew bootRun
```

## Running the tests
This sample project does not contain tests. Using postman or cURL to hit the APIs.

### Upload zip file
```
curl --location 'http://localhost:8080/api/result' \
--form 'allureResults=@"/path/to/allure-results.zip"'
```

### Generate allure-results files
```
curl --location 'http://localhost:8080/api/result/cef55ac7-6095-4470-9cf4-d057a2614be6'
```

## Entities model
![null](https://raw.githubusercontent.com/tnanhd/allure-server-postgres/main/allure.drawio.svg?sanitize=true)