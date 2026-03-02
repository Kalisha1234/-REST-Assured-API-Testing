# API Test Plan - JSONPlaceholder REST API

## 1. Introduction

### 1.1 Purpose
This test plan defines the strategy for automated API testing of the JSONPlaceholder REST API (https://jsonplaceholder.typicode.com/) using REST Assured framework with Java and TestNG.

### 1.2 Scope
Testing covers all 6 resources provided by the JSONPlaceholder API:
- **Posts** (`/posts`) - 100 records
- **Comments** (`/comments`) - 500 records
- **Albums** (`/albums`) - 100 records
- **Photos** (`/photos`) - 5000 records
- **Todos** (`/todos`) - 200 records
- **Users** (`/users`) - 10 records

### 1.3 API Base URL
```
https://jsonplaceholder.typicode.com
```

## 2. Test Strategy

### 2.1 Test Types
| Test Type | Description |
|-----------|-------------|
| Functional | Validate CRUD operations return correct data and status codes |
| Schema Validation | Verify response bodies conform to expected JSON schemas |
| Header Validation | Verify correct Content-Type and other response headers |
| Query Parameter | Validate filtering via query parameters (e.g., `?userId=1`) |
| Negative Testing | Verify proper error handling for invalid requests (e.g., 404 for non-existent resources) |
| Nested Resource | Test nested endpoints (e.g., `/posts/1/comments`, `/users/1/todos`) |
| Parameterized Testing | Data-driven tests using TestNG @DataProvider for multiple IDs, filters, and payloads |

### 2.2 HTTP Methods Covered
| Method | Operation | Expected Status |
|--------|-----------|----------------|
| GET    | Read all / Read by ID / Nested resources | 200 OK |
| GET    | Read non-existent resource | 404 Not Found |
| POST   | Create new resource | 201 Created |
| PUT    | Full update of resource | 200 OK |
| PATCH  | Partial update of resource | 200 OK |
| DELETE | Delete resource | 200 OK |

## 3. Test Cases per Resource

### 3.1 Posts (`/posts`)
| # | Test Case | Method | Endpoint | Validation |
|---|-----------|--------|----------|------------|
| 1 | Get all posts | GET | /posts | Status 200, 100 items returned |
| 2 | Get post by ID | GET | /posts/1 | Status 200, correct fields |
| 3 | Get non-existent post | GET | /posts/9999 | Status 404 |
| 4 | Get post comments | GET | /posts/1/comments | Status 200, postId matches |
| 5 | Filter by userId | GET | /posts?userId=1 | Status 200, all userId=1 |
| 6 | Verify headers | GET | /posts | Content-Type: application/json |
| 7 | Validate JSON schema | GET | /posts/1 | Matches post-schema.json |
| 8 | Create post | POST | /posts | Status 201, data echoed back |
| 9 | Create with empty body | POST | /posts | Status 201, id returned |
| 10 | Full update | PUT | /posts/1 | Status 200, all fields updated |
| 11 | Partial update | PATCH | /posts/1 | Status 200, only title changed |
| 12 | Delete post | DELETE | /posts/1 | Status 200 |

### 3.2 Comments (`/comments`)
| # | Test Case | Method | Endpoint | Validation |
|---|-----------|--------|----------|------------|
| 1 | Get all comments | GET | /comments | Status 200, 500 items |
| 2 | Get comment by ID | GET | /comments/1 | Status 200, correct fields |
| 3 | Get non-existent comment | GET | /comments/9999 | Status 404 |
| 4 | Filter by postId | GET | /comments?postId=1 | Status 200, all postId=1 |
| 5 | Verify headers | GET | /comments | Content-Type check |
| 6 | Validate JSON schema | GET | /comments/1 | Matches comment-schema.json |
| 7 | Create comment | POST | /comments | Status 201 |
| 8 | Full update | PUT | /comments/1 | Status 200 |
| 9 | Partial update | PATCH | /comments/1 | Status 200 |
| 10 | Delete comment | DELETE | /comments/1 | Status 200 |

### 3.3 Albums (`/albums`)
| # | Test Case | Method | Endpoint | Validation |
|---|-----------|--------|----------|------------|
| 1 | Get all albums | GET | /albums | Status 200, 100 items |
| 2 | Get album by ID | GET | /albums/1 | Status 200, correct fields |
| 3 | Get non-existent album | GET | /albums/9999 | Status 404 |
| 4 | Get album photos | GET | /albums/1/photos | Status 200, albumId matches |
| 5 | Filter by userId | GET | /albums?userId=1 | Status 200, all userId=1 |
| 6 | Verify headers | GET | /albums | Content-Type check |
| 7 | Validate JSON schema | GET | /albums/1 | Matches album-schema.json |
| 8 | Create album | POST | /albums | Status 201 |
| 9 | Full update | PUT | /albums/1 | Status 200 |
| 10 | Partial update | PATCH | /albums/1 | Status 200 |
| 11 | Delete album | DELETE | /albums/1 | Status 200 |

### 3.4 Photos (`/photos`)
| # | Test Case | Method | Endpoint | Validation |
|---|-----------|--------|----------|------------|
| 1 | Get all photos | GET | /photos | Status 200, 5000 items |
| 2 | Get photo by ID | GET | /photos/1 | Status 200, correct fields |
| 3 | Get non-existent photo | GET | /photos/99999 | Status 404 |
| 4 | Filter by albumId | GET | /photos?albumId=1 | Status 200, all albumId=1 |
| 5 | Verify headers | GET | /photos | Content-Type check |
| 6 | Validate JSON schema | GET | /photos/1 | Matches photo-schema.json |
| 7 | Create photo | POST | /photos | Status 201 |
| 8 | Full update | PUT | /photos/1 | Status 200 |
| 9 | Partial update | PATCH | /photos/1 | Status 200 |
| 10 | Delete photo | DELETE | /photos/1 | Status 200 |

### 3.5 Todos (`/todos`)
| # | Test Case | Method | Endpoint | Validation |
|---|-----------|--------|----------|------------|
| 1 | Get all todos | GET | /todos | Status 200, 200 items |
| 2 | Get todo by ID | GET | /todos/1 | Status 200, correct fields |
| 3 | Get non-existent todo | GET | /todos/9999 | Status 404 |
| 4 | Filter by userId | GET | /todos?userId=1 | Status 200, all userId=1 |
| 5 | Filter by completed | GET | /todos?completed=true | Status 200, all completed=true |
| 6 | Verify headers | GET | /todos | Content-Type check |
| 7 | Validate JSON schema | GET | /todos/1 | Matches todo-schema.json |
| 8 | Create todo | POST | /todos | Status 201 |
| 9 | Full update | PUT | /todos/1 | Status 200 |
| 10 | Partial update | PATCH | /todos/1 | Status 200 |
| 11 | Delete todo | DELETE | /todos/1 | Status 200 |

### 3.6 Users (`/users`)
| # | Test Case | Method | Endpoint | Validation |
|---|-----------|--------|----------|------------|
| 1 | Get all users | GET | /users | Status 200, 10 items |
| 2 | Get user by ID | GET | /users/1 | Status 200, verify known data |
| 3 | Get non-existent user | GET | /users/9999 | Status 404 |
| 4 | Get user posts | GET | /users/1/posts | Status 200, userId matches |
| 5 | Get user albums | GET | /users/1/albums | Status 200, userId matches |
| 6 | Get user todos | GET | /users/1/todos | Status 200, userId matches |
| 7 | Verify headers | GET | /users | Content-Type check |
| 8 | Validate JSON schema | GET | /users/1 | Matches user-schema.json |
| 9 | Create user | POST | /users | Status 201 |
| 10 | Full update | PUT | /users/1 | Status 200 |
| 11 | Partial update | PATCH | /users/1 | Status 200 |
| 12 | Delete user | DELETE | /users/1 | Status 200 |

## 4. Test Environment

### 4.1 Tools and Technologies
| Component | Technology |
|-----------|-----------|
| Language | Java 17 |
| Build Tool | Maven |
| Testing Framework | TestNG 7.9.0 |
| API Testing Library | REST Assured 5.4.0 |
| Reporting | Allure Reports 2.25.0 |
| CI/CD | GitHub Actions |
| Containerization | Docker |

### 4.2 Test Execution
- **Local**: `mvn clean test`
- **Docker**: `docker-compose up --build`
- **CI/CD**: Automatically triggered on push to `main`/`develop` branches

## 5. Reporting
- **Allure Reports**: Generated after test execution with detailed breakdowns by feature, story, and severity
- **TestNG Reports**: Available in `target/surefire-reports/`
- **CI/CD Artifacts**: Test results uploaded as GitHub Actions artifacts

## 6. Total Test Count
Tests use TestNG `@DataProvider` for parameterization, resulting in 139 total test executions:

| Resource | Test Methods | Parameterized Executions |
|----------|-------------|-------------------------|
| Posts | 11 | 27 |
| Comments | 10 | 25 |
| Albums | 11 | 25 |
| Photos | 10 | 24 |
| Todos | 11 | 25 |
| Users | 12 | 33 |
| **Total** | **65** | **139** |

## 7. Entry/Exit Criteria

### Entry Criteria
- API endpoint (https://jsonplaceholder.typicode.com) is accessible
- Maven dependencies resolved successfully
- Java 17+ installed

### Exit Criteria
- All 139 parameterized test executions pass
- All tests pass with expected status codes
- Allure report generated successfully
- No critical or blocker defects remaining
