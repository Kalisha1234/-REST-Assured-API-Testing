package QA.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }
}
