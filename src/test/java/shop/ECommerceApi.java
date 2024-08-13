package shop;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import secure.SecureUserData;
import pojo.LoginResponse;

import java.util.Arrays;

import static io.restassured.RestAssured.given;

public class ECommerceApi {
    public static void main(String[] args) {
        RequestSpecification reqsb = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .setContentType(ContentType.JSON)
                .build()
        ;

        ResponseSpecification resOK = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build()
                ;

        ResponseSpecification resCreated = new ResponseSpecBuilder()
                .expectStatusCode(201)
                .build()
                ;

        RequestSpecification reqLogin =
        given()
                .spec(reqsb)
                .body(SecureUserData.loginData())
        ;

        LoginResponse loginResponse =
        reqLogin.when()
                .post("api/ecom/auth/login")
        .then()
                .spec(resOK)
                .extract().response().as(LoginResponse.class);
       ;

        String token = loginResponse.getToken();
        String userId = loginResponse.getUserId();


    }
}
