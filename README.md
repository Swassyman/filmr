<div align="center">

<p align="center">
  <img src="src/main/resources/static/banner.png" alt="Filmr">
</p>

*Manage your personal media library.*

Built with **Java**, **Spring Boot**, and **PostgreSQL**

---

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-6DB33F?style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

</div>

---

## About

Filmr is an application for managing your personal media library. It provides a platform for tracking films and television series, maintaining viewing progress. (finally!)

The application is built using Java and Spring Boot with PostgreSQL. (purely cause i wanted to learn it).

---

## Features

### User management

* You can log in and have accounts! (duh..)

### Media library management

* Add media (movies/shows) entries to a personal library.

### Watch status tracking

* Track the current viewing status of each media item.
* Record completion timestamps for watched entries.

---

## System Architecture

> For all the tech nerds who would like to question my sanity,

```text
                 ┌────────────┐
                 │   Client   │
                 └─────┬──────┘
                       │
                       ▼
               ┌──────────────┐
               │   Library    │
               └─────┬────────┘
                     │
                     ▼
      ┌────────────────────────────────┐
      │ Backend + Media API            │
      │ (probably TMDB)                │
      └──────────────┬─────────────────┘
                     │
                     ▼
            ┌─────────────────┐
            │ PostgreSQL      │
            └─────────────────┘
```

---

## Technology Stack

| Component       | Technology                  |
| --------------- | --------------------------- |
| Language        | Java 21                     |
| Framework       | Spring Boot                 |
| Database        | PostgreSQL                  |
| ORM             | Spring Data JPA / Hibernate |
| Build Tool      | Apache Maven                |
| API Style       | REST                        |
| Testing         | JUnit                       |
| Formatting      | Spotless                    |
| Static Analysis | SpotBugs, Checkstyle        |

yea.. im cool like that and all, whatevs

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.filmr
│   │       ├── controller
│   │       ├── service
│   │       ├── repository
│   │       ├── model
│   │       ├── dto
│   │       └── enums
│   └── resources
└── test
```

---

## Code Quality

if you ever wanna contribute or work on this on your own, please keep standards

* **Spotless** ensures consistent formatting across the codebase.
* **Checkstyle** verifies adherence to coding conventions.

---

## Building the Project

Clone me and execute:

```bash
mvn clean verify
```

This command performs the complete verification process, including:

* Dependency resolution
* Compilation
* Unit testing
* Code formatting validation
* Static analysis
* Build verification

---

## Future Enhancements

All my todos:

* Authentication and authorization (muehehe, not yet)
* GraphQL API support (yea.. lord help me)
* Search and filtering capabilities (duh)
* Episode and season progress tracking (even histories!)
* Media metadata integration through external services
* Recommendation features (hmm...)

---

## License

This project is licensed under the MIT License.

---

<div align="center">

<sub>Thanks for stopping by.</sub>

</div>
