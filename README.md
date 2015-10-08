# Spring Boot Starter Kit

My first go at using Spring Boot. This boilerplate project currently supports:

* spring-boot-starter-web
* spring-boot-starter-jdbc
* spring-boot-starter-actuator
* spring-boot-starter-jta-bitronix
* spring-cloud-starter-hystrix-dashboard
* spring-cloud-starter-hystrix
* spring-boot-starter-security
* spring-hateoas
* liquibase
* unirest HTTP client

## Liquibase
Run data migration tasks using Liquibase.

## Hateoas
You can return JSON objects with embedded links.
```
{
  "firstname": "Ringo",
  "lastname": "Star",
  "_links": {
    "self": {
      "href": "http://127.0.0.1:8080/users/7"
    }
  }
}
```

## Hystrix and Hystrix Dashboard
Support for the circuit breaker pattern.

## License
MIT
