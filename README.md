# Restful Booker — API Test Automation

End-to-end CRUD lifecycle tests for [restful-booker.herokuapp.com](https://restful-booker.herokuapp.com/), built with REST Assured + TestNG + Allure.

## Stack
- Java 17
- Maven 3.9+
- REST Assured 5.5.0
- TestNG 7.10.2
- Jackson 2.17.2 — POJO (de)serialization
- Lombok 1.18.34 — model boilerplate
- AssertJ 3.26.3 — fluent assertions, recursive comparison for persistence checks
- Datafaker 2.4.0 — random booking payloads
- Allure 2.29.0 — reporting + auto-attached request/response bodies

## Prerequisites
- JDK 17+ (`java -version`)
- Maven 3.9+ (`mvn -v`)
- Internet access to `https://restful-booker.herokuapp.com`

## Running the tests
```bash
mvn clean test
```

##To generate allure report in browser
allure serve target/allure-results


## To generate HTML allure report
allure generate target/allure-results --clean -o allure-report


## Viewing the Allure report
```bash
mvn allure:serve
```
Generates the report from `target/allure-results` and opens it in your default browser. `Ctrl+C` in the terminal stops the server.

To produce a static HTML report:
```bash
mvn allure:report
# open target/site/allure-maven-plugin/index.html
```

### Sample report

![Allure overview — 18 tests, 100% pass](docs/images/allure-overview.png)

## Project layout
```
src/
  main/java/com/app/qa/restfulbooker/
    client/   BookingClient.java                — HTTP wrappers, no assertions inside
    config/   ConfigLoader.java, Endpoints.java — properties + path constants
    data/     BookingFactory.java               — Datafaker-backed test data
    model/    Booking, BookingDates, AuthRequest, AuthResponse, CreateBookingResponse
    spec/     RequestSpecs.java, ResponseSpecs.java — RequestSpecBuilder/ResponseSpecBuilder factories
  test/java/com/app/qa/restfulbooker/
    base/     BaseTest.java                     — RestAssured timeouts + holds token
    tests/    BookingCrudLifecycleTest.java     — 9 ordered tests
    config/   ConfigLoaderTest.java
    data/     BookingFactoryTest.java
    model/    BookingJsonRoundTripTest.java
  test/resources/
    config.properties, testng.xml, allure.properties, simplelogger.properties
```

## CRUD lifecycle covered
1. **Auth** — POST `/auth`, store cookie token
2. **Create** — POST `/booking`, deep-equal echo against payload
3. **Read** — GET `/booking/{id}`, recursive equals → persistence check
4. **Update (PUT)** — full replacement
5. **Read** — recursive equals → PUT persistence
6. **Patch** — PATCH `firstname`/`lastname` only; untouched fields verified unchanged
7. **Read** — recursive equals → PATCH persistence
8. **Delete** — DELETE `/booking/{id}` (booker returns 201)
9. **404 Verify** — GET `/booking/{id}` after delete returns 404

The lifecycle uses TestNG `dependsOnMethods` so a failure in any step skips downstream steps; the Allure report shows precisely where the chain broke.

## Restful-booker quirks (calibrated for in `ResponseSpecs`)
| Action | Generic REST expectation | Booker actual |
|---|---|---|
| `POST /booking` | 201 Created | **200 OK** |
| `DELETE /booking/{id}` | 204 No Content | **201 Created** |
| Auth token transport | `Authorization: Bearer <t>` | `Cookie: token=<t>` |
| `PATCH` without auth | 401 Unauthorized | **403 Forbidden** |
| `Accept: text/json` | normal JSON response | **418 I'm a teapot** (server easter egg) |

> The teapot quirk above forces `RequestSpecs.defaultSpec()` to set `Accept` as the literal string `"application/json"`. REST Assured's `ContentType.JSON` constant expands the Accept header to include `text/json`, which trips the teapot.

## Design highlights
- **Single Responsibility:** `client/` issues HTTP, `spec/` builds specs, `data/` generates payloads, `tests/` orchestrates + asserts. No layer crosses its boundary.
- **Persistence oracle:** the in-memory `createdBooking` mirrors what the server should hold; every read step uses `usingRecursiveComparison().isEqualTo(...)` against it, so silent server-side mutations would surface.
- **Spec builders:** `RequestSpecs.defaultSpec()` and `RequestSpecs.authSpec(token)` are the only two request-spec factories; tests never construct raw chains.
- **Random test data:** Datafaker per-run; uncovered hardcoding bugs surface immediately.
- **Allure auto-attachment:** `AllureRestAssured` filter is registered inside `RequestSpecs.defaultSpec()` so every request and response body lands in the report without per-test wiring.

