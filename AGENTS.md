# AGENTS.md

## Project state
Fresh Spring Boot skeleton (Initializr output) — no business logic, controllers, or entities yet.
Base package: `dev.serm.novabase_challenge` (note the underscore; `dev.serm.novabase-challenge` is an invalid Java package name per `HELP.md`).

## Stack
- Java 21, Spring Boot 4.0.8-SNAPSHOT (pulls from the Spring Snapshots repo configured in `pom.xml`)
- Maven wrapper (`./mvnw`) — do not require a system-installed Maven
- Dependencies: Spring Web MVC, Spring Data JPA, Validation, H2 (runtime), springdoc-openapi (v3.0.2, pinned separately from the Boot BOM), H2 console

## Commands
- Build: `./mvnw compile`
- Run tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=NovabaseChallengeApplicationTests`
- Run the app: `./mvnw spring-boot:run`
- Package: `./mvnw package`

No lint/format/typecheck tooling is configured — don't invent commands for these.

## Gotchas
- Boot version is a SNAPSHOT, resolved from `https://repo.spring.io/snapshot` — expect slower/less stable dependency resolution than a release version.
- `springdoc-openapi-starter-webmvc-ui` version (3.0.2) is hardcoded and not managed by the Spring Boot BOM; bump it manually if needed.
- `pom.xml` has intentionally empty `<license>`/`<developers>`/`<scm>` overrides to stop them being inherited from the parent POM — keep these empty unless populating real project metadata.
