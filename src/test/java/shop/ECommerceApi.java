package shop;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import pojo.OrderSingle;
import pojo.Orders;
import secure.SecureUserData;
import pojo.LoginResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ECommerceApi {
    public static void main(String[] args) throws InterruptedException {

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

        System.out.println("User " + userId + " authorized!");

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

        System.out.println(lastProductId + " - product created.");

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
        String lastOrderId = jso.get("orders[0]");

        System.out.println("LAST ORDER ID: " + lastOrderId);

        // View order details
        RequestSpecification viewOrderDetailsRequest = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization", token)
                .build()
        ;

        RequestSpecification reqOrderDetails =
        given()
                .spec(viewOrderDetailsRequest)
                .queryParam("id", lastOrderId)
                //.queryParam("id", "66bf490bae2afd4c0b4e099f")
                .log().all()
        ;

        reqOrderDetails.when()
                 .get("/api/ecom/order/get-orders-details")
        .then()
                .log().all()
                .extract().response()
        ;

        // Delete product
        RequestSpecification deleteProductBaseRequest = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization", token)
                .build()
                ;

        RequestSpecification reqADeleteProduct =
        given()
                .spec(deleteProductBaseRequest)
                .pathParam("productId", lastProductId)
        ;

        String productDeletedResponse =
        reqADeleteProduct.when()
                .delete("/api/ecom/product/delete-product/{productId}")
                .then()
                .extract().response().asString()
        ;

        JsonPath jsd = new JsonPath(productDeletedResponse);
        productDeletedResponse = jsd.get("message");

        Assert.assertEquals("Product Deleted Successfully", jsd.get("message"));
        System.out.println(lastProductId + ": " + productDeletedResponse);
    }
}
