# Employee Management (GWT + Spring Boot + Oracle)

1. Run `src/main/resources/schema.sql` in Oracle; edit credentials in `application.yml`.
2. Build:  `mvn clean package`   (GWT compile runs in prepare-package; ~1-2 min)
3. Run:    `java -jar target/employee-mgmt-1.0.0.jar`
4. Open:   http://localhost:8080/

Requires JDK 11 and Maven 3.6+. Run via the jar (not `spring-boot:run`) so gwt-dev stays off the runtime classpath.
