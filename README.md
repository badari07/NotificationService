Notification Service
====================

This project is a Spring Boot microservice that consumes `send-email` events from Apache Kafka and delivers transactional emails via SMTP. Each message is expected to contain the metadata and content for a single outbound email, which the service deserializes and forwards using the JavaMail API.

Key Features
------------
- Kafka consumer wired to the `send-email` topic with a dedicated consumer group.
- JSON payload deserialization into a strongly typed `SendEmailMsgDTO`.
- Email delivery through configurable SMTP settings (defaults to Gmail).
- Pluggable utility (`EmailUtil`) for extending reply-to, content type, and attachments.

Prerequisites
-------------
- Java 17+
- Maven 3.9+ (`mvn` on your PATH)
- Access to a Kafka cluster (local or managed) with the `send-email` topic created
- Valid SMTP credentials that allow programmatic access (e.g., app password for Gmail)

Configuration
-------------
Most runtime options are managed through `src/main/resources/application.properties`. Out of the box the service listens on port `9300`.

Update the SMTP configuration inside `kafkaConsumerConfig` and `EmailUtil` before running in production:
- Replace the placeholder username and password returned in the authenticator within `kafkaConsumerConfig`.
- Adjust the `mail.smtp.*` settings to match your SMTP provider.
- Update the default `from` address in `EmailUtil`.

If you prefer externalized configuration, move the SMTP values into `application.properties` or environment variables and inject them via Spring configuration properties.

Kafka Message Contract
----------------------
The service expects Kafka messages that serialize to the following JSON structure:

```json
{
  "to": "recipient@example.com",
  "from": "sender@example.com",
  "subject": "Subject line",
  "body": "Email body content"
}
```

Publishing a well-formed payload to the `send-email` topic triggers the email dispatch workflow.

Running the Service
-------------------
1. Build the project:
   ```bash
   mvn clean package
   ```
2. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
   or run the shaded jar from `target/NotificationService-0.0.1-SNAPSHOT.jar`.
3. Ensure your Kafka broker is reachable at runtime. The listener will start consuming messages as soon as the service is up.

Testing
-------
Execute the unit test suite with:

```bash
mvn test
```

Next Steps
----------
- Add error handling to route failed deliveries to a dead-letter topic.
- Externalize SMTP credentials and topic names into environment-specific configuration.
- Extend `EmailUtil` to support attachments or HTML templates as needed.
