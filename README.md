# Setup and Installation

## Prerequisites
   
Java 17 or higher

gradle 8.x

Docker & Docker Compose

## Configuration (Mandatory)

For security reasons, sensitive credentials are not included in the repository. You must configure these in your local environment.

Application Properties
Navigate to src/main/resources/application.properties and update the following:

# JWT Security - Change this to a secure 256-bit string inside application.properties
app.jwt.secret=YOUR_CUSTOM_SECRET_KEY_HERE

# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres

spring.datasource.username=YOUR_POSTGRES_USERNAME

spring.datasource.password=YOUR_POSTGRES_PASSWORD

## Docker Environment

If you are using Docker to spin up the database, ensure the credentials in docker-compose.yml match those in your application.properties.



# docker-compose.ymlservices: set your DB Password

POSTGRES_PASSWORD=YOUR_POSTGRES_PASSWORD
      
# Running the Application

## Start the Database:

docker-compose up -d

## Build and Run the App:

gradle build

gradle bootrun
