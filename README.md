## Ryanair Interconnected flights

**What is this Module for?**

This module provides REST end point to get interconnected flight schedule upto one stop

REST API is generated from the Swagger Open API (3.0.3). Sometimes in the IDEA open API will not
pick as Generated Source Root. Need to mark this manually to avoid issues.

go to target > generated-sources > openapi > application and mark the application folder as
Generated Source Root

This application will run in 8080 port

**Setting Up the Module**

This project uses the [Google Java code style](https://google.github.io/styleguide/javaguide.html)
to format the code.

To build:

```bash
mvn compile
```

To create a packaged version, optionally skipping the tests:

```bash
mvn package [-DskipTests]
```

To run:

```bash
mvn clean install
java -jar target/ryanair-interconnecting-flights-0.0.1.jar
```
