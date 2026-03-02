# API Testing With REST Assured

Automated API testing for [JSONPlaceholder](https://jsonplaceholder.typicode.com/) REST API using REST Assured, TestNG, and Allure Reports.

## Tech Stack

- **Java 17** - Programming language
- **Maven** - Build and dependency management
- **REST Assured 5.4.0** - API testing library
- **TestNG 7.9.0** - Test execution framework with `@DataProvider` parameterization
- **Allure Reports 2.25.0** - Test reporting
- **Docker** - Containerization
- **GitHub Actions** - CI/CD pipeline

## API Resources Tested

| Resource | Endpoint | Test Methods | Parameterized Executions |
|----------|----------|-------------|-------------------------|
| Posts | `/posts` | 11 | 27 |
| Comments | `/comments` | 10 | 25 |
| Albums | `/albums` | 11 | 25 |
| Photos | `/photos` | 10 | 24 |
| Todos | `/todos` | 11 | 25 |
| Users | `/users` | 12 | 33 |
| **Total** | | **65** | **139** |

## CRUD Operations Covered

- **GET** - Retrieve all, by ID (parameterized), nested resources, query parameter filtering (parameterized)
- **POST** - Create new resources (parameterized with multiple payloads)
- **PUT** - Full resource update
- **PATCH** - Partial resource update
- **DELETE** - Remove resources

## Validations

- HTTP status codes (200, 201, 404)
- Response body field values
- Response headers (Content-Type)
- JSON schema validation
- Query parameter filtering
- Negative test cases (parameterized with multiple invalid IDs)

## Project Structure

Each resource has its own self-contained package:

```
src/test/
├── java/QA/
│   ├── base/
│   │   └── BaseTest.java                # Base configuration
│   ├── posts/
│   │   ├── PostEndpoints.java           # Endpoint constants
│   │   ├── PostDataGenerator.java       # Test data factory
│   │   └── PostsTest.java              # Tests with @DataProvider
│   ├── comments/
│   │   ├── CommentEndpoints.java
│   │   ├── CommentDataGenerator.java
│   │   └── CommentsTest.java
│   ├── albums/
│   │   ├── AlbumEndpoints.java
│   │   ├── AlbumDataGenerator.java
│   │   └── AlbumsTest.java
│   ├── photos/
│   │   ├── PhotoEndpoints.java
│   │   ├── PhotoDataGenerator.java
│   │   └── PhotosTest.java
│   ├── todos/
│   │   ├── TodoEndpoints.java
│   │   ├── TodoDataGenerator.java
│   │   └── TodosTest.java
│   └── users/
│       ├── UserEndpoints.java
│       ├── UserDataGenerator.java
│       └── UsersTest.java
└── resources/
    ├── schemas/                          # JSON schema files
    ├── testng.xml                        # TestNG suite config
    └── allure.properties
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker (optional)

### Run Tests Locally

```bash
mvn clean test
```

### Generate Allure Report

```bash
mvn allure:serve
```

### Run with Docker

```bash
docker-compose up --build
```

## CI/CD

Tests run automatically via GitHub Actions on:
- Push to `main` or `develop` branches
- Pull requests to `main`

Allure reports are published to GitHub Pages after each run.

## Documentation

- [API Test Plan](API_TEST_PLAN.md) - Detailed test strategy and test cases
- [Report](REPORT.md) - Test execution report and analysis
