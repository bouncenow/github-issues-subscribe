# github-issues-subscribe
Spring Boot app, which allows to subscribe for email updates of github repository issues.
# Installation and config
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
