[![CircleCI](https://circleci.com/gh/mariha/no-name.svg?style=svg&circle-token=a602ace1aa2d081084fff26506f41131483ea483)](https://circleci.com/gh/mariha/no-name)

Instructions
---------------
You will implement a basic REST API. The project is configured with a sqlite database (no-name.db) containing credentials for a Twitter account that can be used to access their REST API. The project contains configuration in the appropriate place which contains an Application Token and Secret for making authenticated requests against Twitter's API.

__Note:__

- The provided sqlite database contains a single table called "twitter_accounts"
- The skeleton applications are already configured to read from this database
- No models/DAOs, endpoints/resources, or url configurations have been defined

__Using the provided skeleton, complete the following:__

Create a REST API endpoint that fulfills the following requirements:
- Accept a "twitter_account.id"
- Lookup the Twitter account by id in the sqlite database
- Use the credentials associated with that account to fetch their home timeline from Twitter's API
- Extract the Screen Name, Text, Date, and Profile Image from each object
- Transform the date into a Unix Timestamp
- Render the result as a JSON response

Create a second endpoint that does the following:
- Accept a "twitter_account.id"
- Lookup the Twitter account by id in the sqlite database
- Accept a text parameter
- Use the credentials associated with the twitter account to send that text as a tweet using Twitter's API

__Requirements/expectations:__
- Assume this is production quality code.
- The URLs should be laid out in a RESTful fashion
- Error handling matters
- etc...



Setup
-------
* Install java 8
* `brew install maven`
* (optional) `export SNYK_API_TOKEN="*********-****-****-****-****"` \
    register and get your API token from [Snyk website](https://snyk.io/). It will be presented in your [Snyk account page](https://snyk.io/account/). \
    If not given, maven error will be reported but won't fail the build.

Build & run
--------
* `mvn test` - to compile and run unit tests
* `mvn package` - to build far jar in `target/homework-*.jar`
* `mvn verify` - to run smoke tests hitting twitter endpoint

* `java -jar target/homework-*.jar` - to start the app, will load config from `config.yml`
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
