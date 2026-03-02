package QA.photos;

import QA.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder API Testing")
@Feature("Photos Resource")
public class PhotosTest extends BaseTest {

    // ==================== Data Providers ====================

    @DataProvider(name = "validPhotoIds")
    public Object[][] validPhotoIds() {
        return new Object[][] {
                {1, 1},
                {50, 1},
                {100, 2},
                {5000, 100}
        };
    }

    @DataProvider(name = "invalidPhotoIds")
    public Object[][] invalidPhotoIds() {
        return new Object[][] {
                {0},
                {5001},
                {99999},
                {-1}
        };
    }

    @DataProvider(name = "albumIdFilter")
    public Object[][] albumIdFilter() {
        return new Object[][] {
                {1, 50},
                {2, 50},
                {50, 50},
                {100, 50}
        };
    }

    @DataProvider(name = "createPhotoData")
    public Object[][] createPhotoData() {
        return new Object[][] {
                {1, "Sunset Photo", "https://via.placeholder.com/600/sunset", "https://via.placeholder.com/150/sunset"},
                {2, "Mountain View", "https://via.placeholder.com/600/mountain", "https://via.placeholder.com/150/mountain"},
                {5, "City Skyline!@#", "https://via.placeholder.com/600/city", "https://via.placeholder.com/150/city"}
        };
    }

    // ==================== GET Tests ====================

    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("GET Operations")
    @Description("Verify fetching all photos returns 5000 photos with status 200")
    public void getAllPhotos() {
        given()
                .spec(requestSpec)
        .when()
                .get(PhotoEndpoints.PHOTOS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(5000))
                .body("[0].albumId", notNullValue())
                .body("[0].title", notNullValue())
                .body("[0].url", notNullValue())
                .body("[0].thumbnailUrl", notNullValue());
    }

    @Test(priority = 2, dataProvider = "validPhotoIds")
    @Severity(SeverityLevel.CRITICAL)
    @Story("GET Operations")
    @Description("Verify fetching a photo by valid ID returns correct data")
    public void getPhotoByValidId(int photoId, int expectedAlbumId) {
        given()
                .spec(requestSpec)
                .pathParam("id", photoId)
        .when()
                .get(PhotoEndpoints.PHOTO_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(photoId))
                .body("albumId", equalTo(expectedAlbumId))
                .body("title", notNullValue())
                .body("url", notNullValue())
                .body("thumbnailUrl", notNullValue());
    }

    @Test(priority = 3, dataProvider = "invalidPhotoIds")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify fetching a non-existent photo returns 404")
    public void getPhotoByInvalidId(int photoId) {
        given()
                .spec(requestSpec)
                .pathParam("id", photoId)
        .when()
                .get(PhotoEndpoints.PHOTO_BY_ID)
        .then()
                .statusCode(404);
    }

    @Test(priority = 4, dataProvider = "albumIdFilter")
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify filtering photos by albumId query parameter")
    public void getPhotosByAlbumIdQueryParam(int albumId, int expectedCount) {
        given()
                .spec(requestSpec)
                .queryParam("albumId", albumId)
        .when()
                .get(PhotoEndpoints.PHOTOS)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(expectedCount))
                .body("albumId", everyItem(equalTo(albumId)));
    }

    @Test(priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Story("GET Operations")
    @Description("Verify response headers for GET photos")
    public void verifyGetPhotosHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get(PhotoEndpoints.PHOTOS)
        .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }

    @Test(priority = 6)
    @Severity(SeverityLevel.MINOR)
    @Story("GET Operations")
    @Description("Verify response conforms to JSON schema for a single photo")
    public void validatePhotoJsonSchema() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get(PhotoEndpoints.PHOTO_BY_ID)
        .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/photo-schema.json"));
    }

    // ==================== POST Tests ====================

    @Test(priority = 7, dataProvider = "createPhotoData")
    @Severity(SeverityLevel.BLOCKER)
    @Story("POST Operations")
    @Description("Verify creating a new photo with various data returns status 201")
    public void createPhoto(int albumId, String title, String url, String thumbnailUrl) {
        Map<String, Object> newPhoto = PhotoDataGenerator.createPhoto(albumId, title, url, thumbnailUrl);

        given()
                .spec(requestSpec)
                .body(newPhoto)
        .when()
                .post(PhotoEndpoints.PHOTOS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("albumId", equalTo(albumId))
                .body("title", equalTo(title));
    }

    // ==================== PUT Tests ====================

    @Test(priority = 8)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PUT Operations")
    @Description("Verify fully updating a photo")
    public void updatePhotoWithPut() {
        Map<String, Object> updatedPhoto = PhotoDataGenerator.createPhoto(
                1, "Updated Photo", "https://via.placeholder.com/600/updated", "https://via.placeholder.com/150/updated"
        );

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(updatedPhoto)
        .when()
                .put(PhotoEndpoints.PHOTO_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Updated Photo"));
    }

    // ==================== PATCH Tests ====================

    @Test(priority = 9)
    @Severity(SeverityLevel.CRITICAL)
    @Story("PATCH Operations")
    @Description("Verify partially updating a photo")
    public void updatePhotoWithPatch() {
        Map<String, Object> partialUpdate = Map.of("title", "Patched Photo Title");

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(partialUpdate)
        .when()
                .patch(PhotoEndpoints.PHOTO_BY_ID)
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Patched Photo Title"));
    }

    // ==================== DELETE Tests ====================

    @Test(priority = 10)
    @Severity(SeverityLevel.CRITICAL)
    @Story("DELETE Operations")
    @Description("Verify deleting a photo returns status 200")
    public void deletePhoto() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .delete(PhotoEndpoints.PHOTO_BY_ID)
        .then()
                .statusCode(200);
    }
}
