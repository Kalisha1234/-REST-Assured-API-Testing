package QA.users;

import QA.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder API Testing")
@Feature("Users Resource")
public class UsersTest extends BaseTest {

    // ==================== Data Providers ====================

    @DataProvider(name = "validUserIds")
    public Object[][] validUserIds() {
        return new Object[][] {
                {1, "Leanne Graham", "Bret"},
                {2, "Ervin Howell", "Antonette"},
                {5, "Chelsey Dietrich", "Kamren"},
                {10, "Clementina DuBuque", "Moriah.Stanton"}
        };
    }

    @DataProvider(name = "invalidUserIds")
    public Object[][] invalidUserIds() {
        return new Object[][] {
                {0},
                {11},
                {9999},
                {-1}
        };
    }

    @DataProvider(name = "userNestedResources")
    public Object[][] userNestedResources() {
        return new Object[][] {
                {1},
                {2},
                {5},
                {10}
        };
    }

    @DataProvider(name = "createUserData")
    public Object[][] createUserData() {
        return new Object[][] {
                {"John Doe", "johndoe", "john@example.com", "1-234-567-8900", "johndoe.com"},
                {"Jane Smith", "janesmith", "jane@example.com", "9-876-543-2100", "janesmith.com"},
                {"Test User!@#", "testuser", "test@example.com", "5-555-555-5555", "testuser.com"}
        };
    }

    // ==================== GET Tests ====================

    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("GET Operations")
    @Description("Verify fetching all users returns 10 users with status 200")
    public void getAllUsers() {
        given()
                .spec(requestSpec)
        .when()
                .get(UserEndpoints.USERS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(10))
                .body("[0].name", notNullValue())
                .body("[0].username", notNullValue())
                .body("[0].email", notNullValue())
                .body("[0].address", notNullValue())
                .body("[0].phone", notNullValue())
                .body("[0].website", notNullValue())
                .body("[0].company", notNullValue());
    }

    @Test(priority = 2, dataProvider = "validUserIds")
    @Severity(SeverityLevel.CRITICAL)
    @Story("GET Operations")
    @Description("Verify fetching a user by valid ID returns correct known data")
    public void getUserByValidId(int userId, String expectedName, String expectedUsername) {
        given()
                .spec(requestSpec)
                .pathParam("id", userId)
        .when()
                .get(UserEndpoints.USER_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("name", equalTo(expectedName))
                .body("username", equalTo(expectedUsername))
                .body("email", notNullValue())
                .body("address", notNullValue())
                .body("company", notNullValue());
    }

    @Test(priority = 3, dataProvider = "invalidUserIds")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching a non-existent user returns 404")
    public void getUserByInvalidId(int userId) {
        given()
                .spec(requestSpec)
                .pathParam("id", userId)
        .when()
                .get(UserEndpoints.USER_BY_ID)
        .then()
                .statusCode(404);
    }

    @Test(priority = 4, dataProvider = "userNestedResources")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching posts for a specific user")
    public void getUserPosts(int userId) {
        given()
                .spec(requestSpec)
                .pathParam("id", userId)
        .when()
                .get(UserEndpoints.USER_POSTS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("userId", everyItem(equalTo(userId)));
    }

    @Test(priority = 5, dataProvider = "userNestedResources")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching albums for a specific user")
    public void getUserAlbums(int userId) {
        given()
                .spec(requestSpec)
                .pathParam("id", userId)
        .when()
                .get(UserEndpoints.USER_ALBUMS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("userId", everyItem(equalTo(userId)));
    }

    @Test(priority = 6, dataProvider = "userNestedResources")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching todos for a specific user")
    public void getUserTodos(int userId) {
        given()
                .spec(requestSpec)
                .pathParam("id", userId)
        .when()
                .get(UserEndpoints.USER_TODOS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("userId", everyItem(equalTo(userId)));
    }

    @Test(priority = 7)
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify response headers for GET users")
    public void verifyGetUsersHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(UserEndpoints.USERS)
        .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }

    @Test(priority = 8)
    @Severity(SeverityLevel.MINOR)
    @Story("GET Operations")
    @Description("Verify response conforms to JSON schema for a single user")
    public void validateUserJsonSchema() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get(UserEndpoints.USER_BY_ID)
        .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    // ==================== POST Tests ====================

    @Test(priority = 9, dataProvider = "createUserData")
    @Severity(SeverityLevel.BLOCKER)
    @Story("POST Operations")
    @Description("Verify creating a new user with various data returns status 201")
    public void createUser(String name, String username, String email, String phone, String website) {
        Map<String, Object> newUser = UserDataGenerator.createUser(name, username, email, phone, website);

        given()
                .spec(requestSpec)
                .body(newUser)
        .when()
                .post(UserEndpoints.USERS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo(name))
                .body("username", equalTo(username))
                .body("email", equalTo(email));
    }

    // ==================== PUT Tests ====================

    @Test(priority = 10)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PUT Operations")
    @Description("Verify fully updating a user")
    public void updateUserWithPut() {
        Map<String, Object> updatedUser = UserDataGenerator.createUser(
                "Jane Doe", "janedoe", "jane@example.com", "9-876-543-2100", "janedoe.com"
        );

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(updatedUser)
        .when()
                .put(UserEndpoints.USER_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Jane Doe"))
                .body("username", equalTo("janedoe"));
    }

    // ==================== PATCH Tests ====================

    @Test(priority = 11)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PATCH Operations")
    @Description("Verify partially updating a user")
    public void updateUserWithPatch() {
        Map<String, Object> partialUpdate = Map.of("name", "Patched Name", "email", "patched@example.com");

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(partialUpdate)
        .when()
                .patch(UserEndpoints.USER_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Patched Name"))
                .body("email", equalTo("patched@example.com"));
    }

    // ==================== DELETE Tests ====================

    @Test(priority = 12)
    @Severity(SeverityLevel.CRITICAL)
    @Story("DELETE Operations")
    @Description("Verify deleting a user returns status 200")
    public void deleteUser() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .delete(UserEndpoints.USER_BY_ID)
        .then()
                .statusCode(200);
    }
}
