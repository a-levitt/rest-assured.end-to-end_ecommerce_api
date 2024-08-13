package shop;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import secure.SecureUserData;
import pojo.LoginResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ECommerceApi {
    public static void main(String[] args) {
        RequestSpecification baseRequest = new RequestSpecBuilder()
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
                .spec(baseRequest)
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

        Map<String, String> productFormParams = new HashMap<>();
        productFormParams.put("productName", "qwerty");
        productFormParams.put("productAddedBy", userId);
        productFormParams.put("productCategory", "fashion");
        productFormParams.put("productSubCategory", "shirts");
        productFormParams.put("productPrice", "11500");
        productFormParams.put("productDescription", "Adidas shirt");
        productFormParams.put("productFor", "women");

        RequestSpecification addProductRequest = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization", token)
                .addFormParams(productFormParams)
                .addMultiPart("productImage", new File("D:\\RESTAssured\\Images\\874ef0bd886ea8fefeb4f1d0be2f7d7e.jpg"))
                .build()
                ;

        String productCreatedResponse =
        addProductRequest.when()
                .post("api/ecom/product/add-product")
        .then()
                .spec(resCreated)
                .extract().response().asString()
        ;

        JsonPath js = new JsonPath(productCreatedResponse);
        String lastProductId = js.get("productId");

        System.out.println(token);
        System.out.println(userId);
        System.out.println(lastProductId);
    }
}
