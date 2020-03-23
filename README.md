[![CircleCI](https://circleci.com/gh/mariha/dropwizard-sandbox.svg?style=svg&circle-token=a602ace1aa2d081084fff26506f41131483ea483)](https://circleci.com/gh/mariha/dropwizard-sandbox)

Simple microservice built with Dropwizard. An endpoint with REST API to post messages and read timeline of a Twitter account. 

The service loads configuration from yaml file. The endpoint with exposed REST API accepts JSON requests, lookups the Twitter account by given id in the sqlite database and makes authenticated requests against Twitter's API. After receiving an answer, it extracts name, date and text from the response and after making simple format transformations sends them back to the user in JSON format.

Setup
-------
The project expects configuration (`config.yml`) and sqlite database (`sandbox.db`) on the classpath. The config defines Application Token and Secret for making authenticated requests against Twitter's REST API. The database contains encrypted credentials for a Twitter account that can be used to access their API. Application token, secret and encryption key can be specified directly in the config or in the environment variables. 

* Install java 8
* `brew install maven`
* Define application token and secret for Twitter API: \
    `export TWITTER_CONSUMER_KEY=******` \
    `export TWITTER_CONSUMER_SECRET=******`
* Define database encryption key \
    `export DB_ENCRYPTION_KEY=******`
* (optional) `export SNYK_API_TOKEN="*********-****-****-****-****"` \
    register and get your API token from [Snyk website](https://snyk.io/). It will be presented in your [Snyk account page](https://snyk.io/account/). \
    If not given, maven error will be reported but won't fail the build.

Build & run
--------
* `mvn test` - to compile and run unit tests
* `mvn package` - to build fat jar in `target/dropwizard-sandbox-*.jar`
* `mvn verify` - to run smoke tests hitting twitter endpoint

* `java -jar target/dropwizard-sandbox--*.jar` - to start the app, will load config from `config.yml`
    * `localhost:8080/api/twitter/{user-id}/tweets` - endpoints to GET timeline and POST a tweet
    * `localhost:8080` - admin
    * `logs/*` - various logs
    * `config.yml` - configuration file
    * `logging.properties` - logging level, right now set to FINE so that http traffic is logged on console

Backlog
----------
- timeline pagination
- logging
- better error handling and tests
- ...
