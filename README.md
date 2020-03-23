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

## Select * from table
```sql
SELECT * FROM postgres;
```

```bash
                  id                  | initial_value | remaining_value
--------------------------------------+---------------+-----------------
 8b76ad5b-e2f8-4411-bc89-2fda39ed3abd |           100 |             100
 b8c5e738-f997-42bf-928b-e32e8c8b182d |           100 |             100
 85ba51f2-a7cf-4b8c-9a7c-2c20543305df |           100 |             100
 376fd5dd-cafe-45fd-9af9-b52f1315d7f6 |           100 |             100
 a7fc627f-9d35-4752-82b8-1f7ff1135e40 |           100 |             100
 1914e15c-ebf1-4977-b2f5-7e9bf26a96f6 |        
   100 |              30
 9377f5c4-be39-493f-9b2e-aebb712015b1 |           100 |              30
 2e655109-3049-4d3c-bd27-0b1c00310513 |           100 |              30
 555fb817-3229-4a86-8b3c-7fe092390a64 |           100 |               0
 adf5be06-fe4b-4bbb-bc70-c1ef1b59d146 |           100 |              30
 e2a91918-6c20-4772-a6ae-ca5a1cbe809c |           100 |               0
 1e7d13b1-6c25-4877-9f2a-3bf10e227abe |           100 |               0
```
