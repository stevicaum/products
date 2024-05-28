# HOW TO RUN
1. Run Postgres, Kafka, Kafka drop: Navigate to the ``/docker/dev`` directory and execute the command ``docker-compose up``
2. Use java corretto 21 and run maven(v3) command ``mvn clean install``
3. Run main class ``org.show.ProductsApp``
4. Default Security: Security is enabled by default.
5. Generate token: Use JwtAdminGenerator to create Admin token or JwtUserGenerator to generate User token for making request:
   ``curl --location 'http://localhost:8080/products?name=wash&page=0&size=10' \
   --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJST0xFX0FETUlOIn0seyJhdXRob3JpdHkiOiJST0xFX1VTRVIifV0sImlzcyI6Imh0dHA6Ly9ib29rcy5jb20iLCJpYXQiOjE3MTUxNjEwNTAsImV4cCI6MTcxNTUyMTA1MH0._G-R9Yi3t1JHEadf1HR7UfY8yFB7IQ1uVZTU2yEqzmk'``
6. Swagger documentation: http://localhost:8080/swagger-ui/index.html
7. Running without Security: To run without security, execute the main class ``org.show.ProductsApp`` with VM options ``-Dspring.profiles.active=no-security``
8. To check kafka messages use Kafka drop: ``http://localhost:9000/``



# HOW TO RUN TESTS
1. Unit Tests: Run Maven build with the command ``mvn clean install``
2. Integration Tests: Execute Maven with the profile ``integration`` using  ``mvn clean install -P integration``

# CALL EXAMPLES
1. GET
``curl -X 'GET' \
   'http://localhost:8080/products?name=wash&page=0&size=10' \
   -H 'accept: */*'``

2. POST
``curl -X 'POST' \
   'http://localhost:8080/products' \
   -H 'accept: */*' \
   -H 'Content-Type: application/json' \
   -d '{
   "name": "Wash and run",
   "price": 4.22
   }'``

3. PUT
``curl -X 'PUT' \
   'http://localhost:8080/products/6' \
   -H 'accept: */*' \
   -H 'Content-Type: application/json' \
   -d '{
   "name": "test",
   "price": 2.2
   }'``