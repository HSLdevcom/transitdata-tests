# transitdata-tests [![Run tests](https://github.com/HSLdevcom/transitdata-tests/actions/workflows/run-tests.yml/badge.svg)](https://github.com/HSLdevcom/transitdata-tests/actions/workflows/run-tests.yml)

Repository containing E2E tests for [Transitdata](https://github.com/HSLdevcom/transitdata) implemented using [microservice-testing-tool](https://github.com/mjaakko/microservice-testing-tool).

## Usage

Tests can be executed either by running the .jar file or the Docker container containing the tests

### Jar

Download latest .jar from [releases](https://github.com/HSLdevcom/transitdata-tests/releases)

```bash
java -jar transitdata-tests.jar
```

### Docker

```bash
docker run -it --rm hsldevcom/transitdata-tests
```
