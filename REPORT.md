# API Testing Report - JSONPlaceholder REST API

## 1. Executive Summary
This report documents the automated API testing of the JSONPlaceholder REST API using REST Assured with Java and TestNG. The test suite covers all 6 resources with comprehensive CRUD operations, totaling 65 test methods that expand to **139 parameterized test executions** via TestNG `@DataProvider`.

## 2. Project Details

| Attribute | Value |
|-----------|-------|
| Project Name | API Testing With REST Assured |
| API Under Test | JSONPlaceholder (https://jsonplaceholder.typicode.com) |
| Testing Framework | REST Assured 5.4.0 + TestNG 7.9.0 |
| Language | Java 17 |
| Build Tool | Maven |
| Reporting | Allure Reports |
| CI/CD | GitHub Actions |
| Containerization | Docker |

## 3. Test Coverage

### 3.1 Resources Tested
| Resource | Endpoint | Records | CRUD Coverage |
|----------|----------|---------|---------------|
| Posts | /posts | 100 | GET, POST, PUT, PATCH, DELETE |
| Comments | /comments | 500 | GET, POST, PUT, PATCH, DELETE |
| Albums | /albums | 100 | GET, POST, PUT, PATCH, DELETE |
| Photos | /photos | 5000 | GET, POST, PUT, PATCH, DELETE |
| Todos | /todos | 200 | GET, POST, PUT, PATCH, DELETE |
| Users | /users | 10 | GET, POST, PUT, PATCH, DELETE |

### 3.2 Validation Types
- **Status Code Validation**: Every test verifies the expected HTTP status code
- **Response Body Validation**: Field values, data types, and content correctness
- **Header Validation**: Content-Type verification for all resources
- **JSON Schema Validation**: Structural validation against predefined schemas
- **Query Parameter Testing**: Filter functionality (userId, postId, albumId, completed)
- **Nested Resource Testing**: Sub-resource endpoints (e.g., /posts/1/comments)
- **Negative Testing**: 404 responses for non-existent resources
- **Parameterized Testing**: Data-driven tests with multiple IDs, filters, and payloads via `@DataProvider`

### 3.3 Test Distribution (Parameterized Executions)
| HTTP Method | Executions |
|-------------|-----------|
| GET | 95 |
| POST | 19 |
| PUT | 6 |
| PATCH | 6 |
| DELETE | 6 |
| **Total** | **139** |

## 4. Architecture

### 4.1 Project Structure
Each resource has its own self-contained package with its test class, endpoint constants, and data generator:
```
src/test/
├── java/QA/
│   ├── base/
│   │   └── BaseTest.java               # Common setup, request/response specs
│   ├── posts/
│   │   ├── PostEndpoints.java           # POST endpoint constants
│   │   ├── PostDataGenerator.java       # POST test data factory
│   │   └── PostsTest.java              # POST CRUD tests with @DataProvider
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
    ├── schemas/                         # JSON schema files for validation
    ├── testng.xml                       # TestNG suite configuration
    └── allure.properties                # Allure configuration
```

### 4.2 Design Patterns
- **Base Test Pattern**: Common configuration inherited by all test classes
- **Request/Response Specification**: Reusable REST Assured specs for consistency
- **Package-per-Resource**: Each resource is self-contained with its own endpoint, data generator, and test class
- **Data-Driven Testing**: TestNG `@DataProvider` for parameterized test execution across multiple inputs
- **Constants Pattern**: All endpoints defined as constants per resource for maintainability

## 5. CI/CD Pipeline
The GitHub Actions workflow automates:
1. Checkout code
2. Set up JDK 17
3. Execute tests via Maven
4. Generate Allure report
5. Publish report to GitHub Pages
6. Upload test artifacts

## 6. Docker Support
- **Dockerfile**: Multi-stage build with Maven + JDK 17
- **docker-compose.yml**: Single-command test execution with volume-mounted results
- Run with: `docker-compose up --build`

## 7. How to Run

### Local Execution
```bash
mvn clean test
```

### Generate Allure Report
```bash
mvn allure:serve
```

### Docker Execution
```bash
docker-compose up --build
```

## 8. Test Results Summary
```
Tests run: 139, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

All 139 parameterized test executions passed successfully across all 6 resources.

## 9. Conclusion
The test suite provides comprehensive coverage of all JSONPlaceholder API resources with 139 parameterized automated test executions. The framework uses a package-per-resource architecture for clean separation of concerns. TestNG `@DataProvider` enables data-driven testing across multiple inputs per test method. Allure Reports provide detailed visibility into test execution results with breakdowns by feature, story, and severity level.
