# Docker MongoDB
```bash
docker run --name mongodb -p 27017:27017 -d mongo:latest
```


# Postgresql
```bash
docker run -d --name postgresdb -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=secret postgres:12
```
## Temporary
```bash
docker run -it --rm --name postgresdb -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=secret postgres:12
```

## Connect to Postgresql  
```bash
docker exec -it postgresdb  psql -Upostgres -a postgres
```

## Create Database
```bash
DROP DATABASE resevation;
CREATE DATABASE resevation;
```

## List all Databases
```bash
\l
```

```bash
postgres=# \l
                                 List of databases
    Name    |  Owner   | Encoding |  Collate   |   Ctype    |   Access privileges
------------+----------+----------+------------+------------+-----------------------
 postgres   | postgres | UTF8     | en_US.utf8 | en_US.utf8 |
 resevation | postgres | UTF8     | en_US.utf8 | en_US.utf8 |
 template0  | postgres | UTF8     | en_US.utf8 | en_US.utf8 | =c/postgres          +
            |          |          |            |            | postgres=CTc/postgres
 template1  | postgres | UTF8     | en_US.utf8 | en_US.utf8 | =c/postgres          +
            |          |          |            |            | postgres=CTc/postgres
(4 rows)
```

## Connect to a Database
```bash
\c reservation
```

## Create table 
```postgresql
CREATE TABLE reservation(
   ID   SERIAL PRIMARY KEY,
   NAME varchar(255) NOT NULL
);
```

# Describe Table
```postgresql
\d reservation
```

## Select * from table
```sql
SELECT * FROM reservation;
```

```bash
 id |   name
----+----------
 33 | Madhura
 34 | Ria
 35 | Olga
 36 | Violetta
 37 | Marcin
 38 | Stéphane
 39 | Josh
 40 | Dr. Syer
(8 rows)
```

# Httpie Call Stream API
```bash
http --stream  :8080/greetings/flux
```

# WebSocket
## UI
Open Browser Web Developer and check console output. 
http://localhost:8080/ws.html

## Simple Web Socket Client 
Browser Plugin
URL:
```bash
ws://localhost:8080/ws/greetings
```
Request:
```bash
Cool
```

## Websocat
### PreConditions 
```bash
brew install websocat
```

```bash
websocat ws://localhost:8080/ws/greetings
Welcome to my Socket
Hello Welcome to my Socket  @ 2020-04-07T13:50:06.446784Z
Hello Welcome to my Socket  @ 2020-04-07T13:50:07.449430Z
Hello Welcome to my Socket  @ 2020-04-07T13:50:08.453231Z
Hello Welcome to my Socket  @ 2020-04-07T13:50:09.457839Z
```