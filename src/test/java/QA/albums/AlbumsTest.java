package QA.albums;

import QA.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder API Testing")
@Feature("Albums Resource")
public class AlbumsTest extends BaseTest {

    // ==================== Data Providers ====================

    @DataProvider(name = "validAlbumIds")
    public Object[][] validAlbumIds() {
        return new Object[][] {
                {1, 1},
                {10, 1},
                {50, 5},
                {100, 10}
        };
    }

    @DataProvider(name = "invalidAlbumIds")
    public Object[][] invalidAlbumIds() {
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

    @DataProvider(name = "createAlbumData")
    public Object[][] createAlbumData() {
        return new Object[][] {
                {1, "My Vacation Photos"},
                {2, "Work Conference 2024"},
                {5, "Album With Special Chars!@#"}
        };
    }

    // ==================== GET Tests ====================

    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("GET Operations")
    @Description("Verify fetching all albums returns 100 albums with status 200")
    public void getAllAlbums() {
        given()
                .spec(requestSpec)
        .when()
                .get(AlbumEndpoints.ALBUMS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(100))
                .body("[0].id", equalTo(1))
                .body("[0].userId", notNullValue())
                .body("[0].title", notNullValue());
    }

    @Test(priority = 2, dataProvider = "validAlbumIds")
    @Severity(SeverityLevel.CRITICAL)
    @Story("GET Operations")
    @Description("Verify fetching an album by valid ID returns correct data")
    public void getAlbumByValidId(int albumId, int expectedUserId) {
        given()
                .spec(requestSpec)
                .pathParam("id", albumId)
        .when()
                .get(AlbumEndpoints.ALBUM_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(albumId))
                .body("userId", equalTo(expectedUserId))
                .body("title", notNullValue());
    }

    @Test(priority = 3, dataProvider = "invalidAlbumIds")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching a non-existent album returns 404")
    public void getAlbumByInvalidId(int albumId) {
        given()
                .spec(requestSpec)
                .pathParam("id", albumId)
        .when()
                .get(AlbumEndpoints.ALBUM_BY_ID)
        .then()
                .statusCode(404);
    }

    @Test(priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching photos for a specific album")
    public void getAlbumPhotos() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get(AlbumEndpoints.ALBUM_PHOTOS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].albumId", equalTo(1));
    }

    @Test(priority = 5, dataProvider = "userIdFilter")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify filtering albums by userId query parameter")
    public void getAlbumsByUserIdQueryParam(int userId, int expectedCount) {
        given()
                .spec(requestSpec)
                .queryParam("userId", userId)
        .when()
                .get(AlbumEndpoints.ALBUMS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(expectedCount))
                .body("userId", everyItem(equalTo(userId)));
    }

    @Test(priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify response headers for GET albums")
    public void verifyGetAlbumsHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(AlbumEndpoints.ALBUMS)
        .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }

    @Test(priority = 7)
    @Severity(SeverityLevel.MINOR)
    @Story("GET Operations")
    @Description("Verify response conforms to JSON schema for a single album")
    public void validateAlbumJsonSchema() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get(AlbumEndpoints.ALBUM_BY_ID)
        .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/album-schema.json"));
    }

    // ==================== POST Tests ====================

    @Test(priority = 8, dataProvider = "createAlbumData")
    @Severity(SeverityLevel.BLOCKER)
    @Story("POST Operations")
    @Description("Verify creating a new album with various data returns status 201")
    public void createAlbum(int userId, String title) {
        Map<String, Object> newAlbum = AlbumDataGenerator.createAlbum(userId, title);

        given()
                .spec(requestSpec)
                .body(newAlbum)
        .when()
                .post(AlbumEndpoints.ALBUMS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(userId))
                .body("title", equalTo(title));
    }

    // ==================== PUT Tests ====================

    @Test(priority = 9)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PUT Operations")
    @Description("Verify fully updating an album")
    public void updateAlbumWithPut() {
        Map<String, Object> updatedAlbum = AlbumDataGenerator.createAlbum(1, "Updated Album Title");

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(updatedAlbum)
        .when()
                .put(AlbumEndpoints.ALBUM_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Updated Album Title"));
    }

    // ==================== PATCH Tests ====================

    @Test(priority = 10)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PATCH Operations")
    @Description("Verify partially updating an album")
    public void updateAlbumWithPatch() {
        Map<String, Object> partialUpdate = Map.of("title", "Patched Album Title");

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(partialUpdate)
        .when()
                .patch(AlbumEndpoints.ALBUM_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Patched Album Title"));
    }

    // ==================== DELETE Tests ====================

    @Test(priority = 11)
    @Severity(SeverityLevel.CRITICAL)
    @Story("DELETE Operations")
    @Description("Verify deleting an album returns status 200")
    public void deleteAlbum() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .delete(AlbumEndpoints.ALBUM_BY_ID)
        .then()
                .statusCode(200);
    }
}
