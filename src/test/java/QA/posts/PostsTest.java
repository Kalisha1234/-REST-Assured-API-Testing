package QA.posts;

import QA.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder API Testing")
@Feature("Posts Resource")
public class PostsTest extends BaseTest {

    // ==================== Data Providers ====================

    @DataProvider(name = "validPostIds")
    public Object[][] validPostIds() {
        return new Object[][] {
                {1, 1},
                {5, 1},
                {50, 5},
                {100, 10}
        };
    }

    @DataProvider(name = "invalidPostIds")
    public Object[][] invalidPostIds() {
        return new Object[][] {
                {0},
                {101},
                {9999},
                {-1}
        };
    }

    @DataProvider(name = "userIdFilter")
    public Object[][] userIdFilter() {
        return new Object[][] {
                {1, 10},
                {2, 10},
                {5, 10},
                {10, 10}
        };
    }

    @DataProvider(name = "createPostData")
    public Object[][] createPostData() {
        return new Object[][] {
                {1, "First Test Post", "Body of the first test post"},
                {2, "Second Test Post", "Body of the second test post"},
                {5, "Post With Special Chars!@#", "Body with special characters: <>&"},
                {1, "", ""}
        };
    }

    // ==================== GET Tests ====================

    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("GET Operations")
    @Description("Verify fetching all posts returns 100 posts with status 200")
    public void getAllPosts() {
        given()
                .spec(requestSpec)
        .when()
                .get(PostEndpoints.POSTS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(100))
                .body("[0].id", equalTo(1))
                .body("[0].userId", notNullValue())
                .body("[0].title", notNullValue())
                .body("[0].body", notNullValue());
    }

    @Test(priority = 2, dataProvider = "validPostIds")
    @Severity(SeverityLevel.CRITICAL)
    @Story("GET Operations")
    @Description("Verify fetching a post by valid ID returns correct data")
    public void getPostByValidId(int postId, int expectedUserId) {
        given()
                .spec(requestSpec)
                .pathParam("id", postId)
        .when()
                .get(PostEndpoints.POST_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("userId", equalTo(expectedUserId))
                .body("title", notNullValue())
                .body("body", notNullValue());
    }

    @Test(priority = 3, dataProvider = "invalidPostIds")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching a non-existent post returns 404")
    public void getPostByInvalidId(int postId) {
        given()
                .spec(requestSpec)
                .pathParam("id", postId)
        .when()
                .get(PostEndpoints.POST_BY_ID)
        .then()
                .statusCode(404);
    }

    @Test(priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching comments for a specific post")
    public void getPostComments() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get(PostEndpoints.POST_COMMENTS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].postId", equalTo(1))
                .body("[0].email", notNullValue());
    }

    @Test(priority = 5, dataProvider = "userIdFilter")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify filtering posts by userId query parameter")
    public void getPostsByUserIdQueryParam(int userId, int expectedCount) {
        given()
                .spec(requestSpec)
                .queryParam("userId", userId)
        .when()
                .get(PostEndpoints.POSTS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(expectedCount))
                .body("userId", everyItem(equalTo(userId)));
    }

    @Test(priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify response headers for GET posts request")
    public void verifyGetPostsHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(PostEndpoints.POSTS)
        .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }

    @Test(priority = 7)
    @Severity(SeverityLevel.MINOR)
    @Story("GET Operations")
    @Description("Verify response conforms to JSON schema for a single post")
    public void validatePostJsonSchema() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get(PostEndpoints.POST_BY_ID)
        .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    // ==================== POST Tests ====================

    @Test(priority = 8, dataProvider = "createPostData")
    @Severity(SeverityLevel.BLOCKER)
    @Story("POST Operations")
    @Description("Verify creating a new post with various data returns status 201")
    public void createPost(int userId, String title, String body) {
        Map<String, Object> newPost = PostDataGenerator.createPost(userId, title, body);

        given()
                .spec(requestSpec)
                .body(newPost)
        .when()
                .post(PostEndpoints.POSTS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(userId))
                .body("title", equalTo(title))
                .body("body", equalTo(body));
    }

    // ==================== PUT Tests ====================

    @Test(priority = 9)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PUT Operations")
    @Description("Verify fully updating a post replaces all fields")
    public void updatePostWithPut() {
        Map<String, Object> updatedPost = PostDataGenerator.createPost(
                1, "Updated Title", "Updated body content"
        );

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(updatedPost)
        .when()
                .put(PostEndpoints.POST_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Updated Title"))
                .body("body", equalTo("Updated body content"))
                .body("userId", equalTo(1));
    }

    // ==================== PATCH Tests ====================

    @Test(priority = 10)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PATCH Operations")
    @Description("Verify partially updating a post modifies only specified fields")
    public void updatePostWithPatch() {
        Map<String, Object> partialUpdate = Map.of("title", "Patched Title");

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(partialUpdate)
        .when()
                .patch(PostEndpoints.POST_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Patched Title"))
                .body("body", notNullValue());
    }

    // ==================== DELETE Tests ====================

    @Test(priority = 11)
    @Severity(SeverityLevel.CRITICAL)
    @Story("DELETE Operations")
    @Description("Verify deleting a post returns status 200")
    public void deletePost() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .delete(PostEndpoints.POST_BY_ID)
        .then()
                .statusCode(200);
    }
}
