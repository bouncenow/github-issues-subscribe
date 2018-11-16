# github-issues-subscribe
Spring Boot app, which allows to subscribe for email updates of github repository issues.
## Installation and config
Build requires access to PostgreSQL db instance (to allow Jooq to generate typesafe DB classes). You either can set db credentials
in gradle.properties, or set DATABASE_URL environment variable (as it is done on Heroku)

To run an app, several environment variables should be set:
```
MAIL_HOST
MAIL_USERNAME
MAIL_PASSWORD
MAIL_FROM
GITHUB_API_TOKEN
```
First three are credentials for accessing SMTP server from which email will be sent, `MAIL_FROM` which address will be used for
'From' value of emails.
Access to db for runnning application is controlled by app properties `spring.datasource.{url,username,password}`. Other config options
can be found in `application.yml` file.

## Endpoints
- GET `/repositories?language=:language&count=:count`
Returns list of repositories with given language sorted by stars. Example:
```
GET ${baseUrl}/repositories?language=javascript&count=3
```
Result:
```
[
  {
    "name": "freeCodeCamp",
    "description": "The https://freeCodeCamp.org open source codebase and curriculum. Learn to code for free together with millions of people.",
    "owner": "freeCodeCamp",
    "stars": 296104,
    "forks": 19909
  },
  {
    "name": "vue",
    "description": "🖖 A progressive, incrementally-adoptable JavaScript framework for building UI on the web.",
    "owner": "vuejs",
    "stars": 119177,
    "forks": 16589
  },
  {
    "name": "react",
    "description": "A declarative, efficient, and flexible JavaScript library for building user interfaces.",
    "owner": "facebook",
    "stars": 115631,
    "forks": 20173
  }
]
```
- POST `/subscriptions`
Subscribe to new issues of repository via receiving emails. Example:
```
POST ${baseUrl}/subscriptions
Content-Type: application/json

{
  "email": "email@example.com",
  "repository": {
    "name": "node",
    "owner": "nodejs"
  },
  "checkInterval": "PT1M"
}
```
where `checkInterval` is a ISO duration.
Result:
```
{
  "id": 2131,
  "email": "email@example.com",
  "repository": {
    "name": "node",
    "owner": "nodejs"
  },
  "checkInterval": "PT1M"
}
```
After that, each `checkInterval` app will check for new issues in repository, and will email them to given email address.
