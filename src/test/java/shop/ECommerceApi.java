package shop;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import pojo.OrderSingle;
import pojo.Orders;
import secure.SecureUserData;
import pojo.LoginResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ECommerceApi {
    public static void main(String[] args) {

        // Login

        RequestSpecification baseRequest = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .setContentType(ContentType.JSON)
                .build()
        ;


        RequestSpecification reqLogin =
        given()
                .spec(baseRequest)
                .body(SecureUserData.loginData())
        ;

        LoginResponse loginResponse =
        reqLogin.when()
                .post("/api/ecom/auth/login")
        .then()
                .extract().response().as(LoginResponse.class)
       ;

        String token = loginResponse.getToken();
        String userId = loginResponse.getUserId();

        // Add Product

        RequestSpecification addProductBaseRequest = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization", token)
                .build()
        ;

        RequestSpecification reqAddProduct =
        given()
                .spec(addProductBaseRequest)
                .param("productName", "qwerty")
                .param("productAddedBy", userId)
                .param("productCategory", "fashion")
                .param("productSubCategory", "shirts")
                .param("productPrice", "11500")
                .param("productDescription", "Adidas shirt")
                .param("productFor", "women")
                .multiPart("productImage", new File("D:\\RESTAssured\\Images\\874ef0bd886ea8fefeb4f1d0be2f7d7e.jpg"))
        ;

        String productCreatedResponse =
        reqAddProduct.when()
                .post("/api/ecom/product/add-product")
        .then()
                .extract().response().asString()
        ;

        JsonPath jsp = new JsonPath(productCreatedResponse);
        String lastProductId = jsp.get("productId");

        // Create Order
        RequestSpecification createOrderBaseRequest = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization", token)
                .setContentType(ContentType.JSON)
                .build()
        ;

        OrderSingle order = new OrderSingle();
        order.setCountry("Montenegro");
        order.setProductOrderedId(lastProductId);
        List<OrderSingle> listOfOrders = new ArrayList<>();
        listOfOrders.add(order);
        Orders orders = new Orders();
        orders.setOrders(listOfOrders);

        RequestSpecification reqCreateOrder =
        given()
                .spec(createOrderBaseRequest)
                .body(orders)
        ;

        String orderCreatedResponse =
        reqCreateOrder.when()
                .post("/api/ecom/order/create-order")
        .then()
                .extract().response().asString()
        ;

        JsonPath jso = new JsonPath(orderCreatedResponse);
        String lastOrderId = jso.get("productOrderId[0]");

        // View order details

    }
}
