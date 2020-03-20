## Email API

This is a microservice written with Java Spring Boot which allows you to very quickly
send notifications via email messages to an SMTP server.

Features:
* Supports whitelisting of email domains
* Supports Slack notifications upon completion
* Supports TLS connections to SMTP


## Example

You can send requests over HTTP e.g.

```json
{
  "to": ["john.smith@example.com"],
  "cc": [],
  "bcc": [],
  "subject": "Email subject",
  "body":"<p>I support HTML</p>"
}
```