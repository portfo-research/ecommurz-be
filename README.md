## Ecommurz
Welcome to ecommurz, ecommurz is a example project of e-commerce

# Requirement
* Java 11 or higher
* Gradle 7.2 or higher
* PostgresSQL

## Tech Stack
* Springboot
* Json Web Token (JWT)

## Setup DB
1. Open Pgadmin
2. Create database with specification in "application-local.yaml"
3. Choose database and right click and restore
4. Choose backup file at /ecommurz-be/db/eccomurz.backup
5. Check privileges
6. Uncheck Verbose Message

## Run App
Run this application using this
```bootRun --args='--spring.profiles.active=local```