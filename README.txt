
* `mvn test` - to compile and run unit tests
* `mvn package` - to build far jar in `target/homework-*.jar`
* `mvn verify` - to run smoke tests hitting twitter endpoint

* `java -jar target/homework-*.jar` - to start the app, will load config from `config.yml`
    `localhost:8080/api/twitter/{user-id}/tweets` - endpoints to GET timeline and POST a tweet
    `localhost:8080` - admin
    `logs/*` - various logs
    `config.yml` - configuration file
    `logging.properties` - right not set logging level to FINE so that http traffic is logged on console

Backlog:
- timeline pagination
- logging
- better error handling and tests
- ...
