package QA.comments;

import QA.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder API Testing")
@Feature("Comments Resource")
public class CommentsTest extends BaseTest {

    // ==================== Data Providers ====================

    @DataProvider(name = "validCommentIds")
    public Object[][] validCommentIds() {
        return new Object[][] {
                {1, 1},
                {5, 1},
                {100, 20},
                {500, 100}
        };
    }

    @DataProvider(name = "invalidCommentIds")
    public Object[][] invalidCommentIds() {
        return new Object[][] {
                {0},
                {501},
                {9999},
                {-1}
        };
    }

    @DataProvider(name = "postIdFilter")
    public Object[][] postIdFilter() {
        return new Object[][] {
                {1, 5},
                {2, 5},
                {50, 5},
                {100, 5}
        };
    }

    @DataProvider(name = "createCommentData")
    public Object[][] createCommentData() {
        return new Object[][] {
                {1, "Test Comment", "test@example.com", "This is a test comment body"},
                {2, "Another Comment", "another@example.com", "Another comment body"},
                {50, "Comment with Specials!@#", "special@test.com", "Body with <special> chars"}
        };
    }

    // ==================== GET Tests ====================

    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("GET Operations")
    @Description("Verify fetching all comments returns 500 comments with status 200")
    public void getAllComments() {
        given()
                .spec(requestSpec)
        .when()
                .get(CommentEndpoints.COMMENTS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(500))
                .body("[0].postId", notNullValue())
                .body("[0].name", notNullValue())
                .body("[0].email", notNullValue())
                .body("[0].body", notNullValue());
    }

    @Test(priority = 2, dataProvider = "validCommentIds")
    @Severity(SeverityLevel.CRITICAL)
    @Story("GET Operations")
    @Description("Verify fetching a comment by valid ID returns correct data")
    public void getCommentByValidId(int commentId, int expectedPostId) {
        given()
                .spec(requestSpec)
                .pathParam("id", commentId)
        .when()
                .get(CommentEndpoints.COMMENT_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(commentId))
                .body("postId", equalTo(expectedPostId))
                .body("name", notNullValue())
                .body("email", notNullValue())
                .body("body", notNullValue());
    }

    @Test(priority = 3, dataProvider = "invalidCommentIds")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching a non-existent comment returns 404")
    public void getCommentByInvalidId(int commentId) {
        given()
                .spec(requestSpec)
                .pathParam("id", commentId)
        .when()
                .get(CommentEndpoints.COMMENT_BY_ID)
        .then()
                .statusCode(404);
    }

    @Test(priority = 4, dataProvider = "postIdFilter")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify filtering comments by postId query parameter")
    public void getCommentsByPostIdQueryParam(int postId, int expectedCount) {
        given()
                .spec(requestSpec)
                .queryParam("postId", postId)
        .when()
                .get(CommentEndpoints.COMMENTS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(expectedCount))
                .body("postId", everyItem(equalTo(postId)));
    }

    @Test(priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify response headers for GET comments")
    public void verifyGetCommentsHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(CommentEndpoints.COMMENTS)
        .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }

    @Test(priority = 6)
    @Severity(SeverityLevel.MINOR)
    @Story("GET Operations")
    @Description("Verify response conforms to JSON schema for a single comment")
    public void validateCommentJsonSchema() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get(CommentEndpoints.COMMENT_BY_ID)
        .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/comment-schema.json"));
    }

    // ==================== POST Tests ====================

    @Test(priority = 7, dataProvider = "createCommentData")
    @Severity(SeverityLevel.BLOCKER)
    @Story("POST Operations")
    @Description("Verify creating a new comment with various data returns status 201")
    public void createComment(int postId, String name, String email, String body) {
        Map<String, Object> newComment = CommentDataGenerator.createComment(postId, name, email, body);

        given()
                .spec(requestSpec)
                .body(newComment)
        .when()
                .post(CommentEndpoints.COMMENTS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("postId", equalTo(postId))
                .body("name", equalTo(name))
                .body("email", equalTo(email))
                .body("body", equalTo(body));
    }

    // ==================== PUT Tests ====================

    @Test(priority = 8)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PUT Operations")
    @Description("Verify fully updating a comment")
    public void updateCommentWithPut() {
        Map<String, Object> updatedComment = CommentDataGenerator.createComment(
                1, "Updated Comment", "updated@example.com", "Updated comment body"
        );

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(updatedComment)
        .when()
                .put(CommentEndpoints.COMMENT_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Updated Comment"))
                .body("email", equalTo("updated@example.com"));
    }

    // ==================== PATCH Tests ====================

    @Test(priority = 9)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PATCH Operations")
    @Description("Verify partially updating a comment")
    public void updateCommentWithPatch() {
        Map<String, Object> partialUpdate = Map.of("name", "Patched Comment Name");

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(partialUpdate)
        .when()
                .patch(CommentEndpoints.COMMENT_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Patched Comment Name"));
    }

    // ==================== DELETE Tests ====================

    @Test(priority = 10)
    @Severity(SeverityLevel.CRITICAL)
    @Story("DELETE Operations")
    @Description("Verify deleting a comment returns status 200")
    public void deleteComment() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .delete(CommentEndpoints.COMMENT_BY_ID)
        .then()
                .statusCode(200);
    }
}
