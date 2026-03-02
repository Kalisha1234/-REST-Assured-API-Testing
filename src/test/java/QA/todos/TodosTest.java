package QA.todos;

import QA.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder API Testing")
@Feature("Todos Resource")
public class TodosTest extends BaseTest {

    // ==================== Data Providers ====================

    @DataProvider(name = "validTodoIds")
    public Object[][] validTodoIds() {
        return new Object[][] {
                {1, 1},
                {10, 1},
                {100, 5},
                {200, 10}
        };
    }

    @DataProvider(name = "invalidTodoIds")
    public Object[][] invalidTodoIds() {
        return new Object[][] {
                {0},
                {201},
                {9999},
                {-1}
        };
    }

    @DataProvider(name = "userIdFilter")
    public Object[][] userIdFilter() {
        return new Object[][] {
                {1, 20},
                {2, 20},
                {5, 20},
                {10, 20}
        };
    }

    @DataProvider(name = "completedFilter")
    public Object[][] completedFilter() {
        return new Object[][] {
                {true},
                {false}
        };
    }

    @DataProvider(name = "createTodoData")
    public Object[][] createTodoData() {
        return new Object[][] {
                {1, "Buy groceries", false},
                {2, "Complete project report", true},
                {5, "Todo with specials !@#$", false}
        };
    }

    // ==================== GET Tests ====================

    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("GET Operations")
    @Description("Verify fetching all todos returns 200 todos with status 200")
    public void getAllTodos() {
        given()
                .spec(requestSpec)
        .when()
                .get(TodoEndpoints.TODOS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(200))
                .body("[0].userId", notNullValue())
                .body("[0].title", notNullValue())
                .body("[0].completed", notNullValue());
    }

    @Test(priority = 2, dataProvider = "validTodoIds")
    @Severity(SeverityLevel.CRITICAL)
    @Story("GET Operations")
    @Description("Verify fetching a todo by valid ID returns correct data")
    public void getTodoByValidId(int todoId, int expectedUserId) {
        given()
                .spec(requestSpec)
                .pathParam("id", todoId)
        .when()
                .get(TodoEndpoints.TODO_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(todoId))
                .body("userId", equalTo(expectedUserId))
                .body("title", notNullValue())
                .body("completed", notNullValue());
    }

    @Test(priority = 3, dataProvider = "invalidTodoIds")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching a non-existent todo returns 404")
    public void getTodoByInvalidId(int todoId) {
        given()
                .spec(requestSpec)
                .pathParam("id", todoId)
        .when()
                .get(TodoEndpoints.TODO_BY_ID)
        .then()
                .statusCode(404);
    }

    @Test(priority = 4, dataProvider = "userIdFilter")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify filtering todos by userId query parameter")
    public void getTodosByUserIdQueryParam(int userId, int expectedCount) {
        given()
                .spec(requestSpec)
                .queryParam("userId", userId)
        .when()
                .get(TodoEndpoints.TODOS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(expectedCount))
                .body("userId", everyItem(equalTo(userId)));
    }

    @Test(priority = 5, dataProvider = "completedFilter")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify filtering todos by completed status")
    public void getTodosByCompletedStatus(boolean completed) {
        given()
                .spec(requestSpec)
                .queryParam("completed", completed)
        .when()
                .get(TodoEndpoints.TODOS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("completed", everyItem(equalTo(completed)));
    }

    @Test(priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify response headers for GET todos")
    public void verifyGetTodosHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(TodoEndpoints.TODOS)
        .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }

    @Test(priority = 7)
    @Severity(SeverityLevel.MINOR)
    @Story("GET Operations")
    @Description("Verify response conforms to JSON schema for a single todo")
    public void validateTodoJsonSchema() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get(TodoEndpoints.TODO_BY_ID)
        .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/todo-schema.json"));
    }

    // ==================== POST Tests ====================

    @Test(priority = 8, dataProvider = "createTodoData")
    @Severity(SeverityLevel.BLOCKER)
    @Story("POST Operations")
    @Description("Verify creating a new todo with various data returns status 201")
    public void createTodo(int userId, String title, boolean completed) {
        Map<String, Object> newTodo = TodoDataGenerator.createTodo(userId, title, completed);

        given()
                .spec(requestSpec)
                .body(newTodo)
        .when()
                .post(TodoEndpoints.TODOS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(userId))
                .body("title", equalTo(title))
                .body("completed", equalTo(completed));
    }

    // ==================== PUT Tests ====================

    @Test(priority = 9)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PUT Operations")
    @Description("Verify fully updating a todo")
    public void updateTodoWithPut() {
        Map<String, Object> updatedTodo = TodoDataGenerator.createTodo(1, "Updated Todo", true);

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(updatedTodo)
        .when()
                .put(TodoEndpoints.TODO_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Updated Todo"))
                .body("completed", equalTo(true));
    }

    // ==================== PATCH Tests ====================

    @Test(priority = 10)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PATCH Operations")
    @Description("Verify partially updating a todo")
    public void updateTodoWithPatch() {
        Map<String, Object> partialUpdate = Map.of("completed", true);

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(partialUpdate)
        .when()
                .patch(TodoEndpoints.TODO_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("completed", equalTo(true));
    }

    // ==================== DELETE Tests ====================

    @Test(priority = 11)
    @Severity(SeverityLevel.CRITICAL)
    @Story("DELETE Operations")
    @Description("Verify deleting a todo returns status 200")
    public void deleteTodo() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .delete(TodoEndpoints.TODO_BY_ID)
        .then()
                .statusCode(200);
    }
}
