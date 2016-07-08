package org.sourceit.post;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class PostTest {

    @BeforeClass
    public void init() {
        RestAssured.baseURI = "http://jsonplaceholder.typicode.com";
        RestAssured.basePath = "/posts";

    }

    @Test(groups = {"get"})
    public void testGetPosts() {
        Assert.assertEquals(
                RestAssured.given().log().all().get("")
                        .then()
                        .contentType(ContentType.JSON)
                        .extract()
                        .response()
                        .body()
                        .jsonPath()
                        .getList("")
                        .size(), 100);
    }

    @Test(parameters = {"userId", "id", "title"}, groups = {"get"})
    public void testGetFirstPost(String userId,
                                 String id,
                                 String title) {
        RestAssured.given().log().all().get("/1")
                .then()
                .body("userId", Matchers.equalTo(Integer.valueOf(userId)))
                .body("id", Matchers.equalTo(Integer.valueOf(id)))
                .body("title", Matchers.equalTo(title));
    }

    @Test(parameters = {"myUserId"}, groups = {"change"}, dependsOnGroups = {"get"})
    public void testCreatePost(Integer userId) {
        RestAssured.given().given().log().all().contentType(ContentType.JSON)
                .body("{\"title\": \"foo\",\n" +
                        "\"body\": \"bar\",\n" +
                        "\"userId\":" + userId + "}")
                .post("").
                then().body("userId", Matchers.equalTo(userId));
    }

    @Test(groups = {"change"}, dependsOnGroups = {"get"})
    public void testUpdatePost() {
        RestAssured.given().given().log().all().contentType(ContentType.JSON)
                .body("{\"id\": 1,\"title\": \"foo\",\n" +
                        "\"body\": \"bar\",\n" +
                        "\"userId\":" + 345 + "}")
                .put("/1").
                then().body("userId", Matchers.equalTo(345));
    }

    @Test(dependsOnGroups = {"get", "change"})
    public void testDeletePost() {
        String resp = RestAssured.given().given().log().all().contentType(ContentType.JSON)
                .body("{\"id\": 1,\"title\": \"foo\",\n" +
                        "\"body\": \"bar\",\n" +
                        "\"userId\":" + 345 + "}")
                .delete("/1").
                        then().extract().response().getBody().prettyPeek().asString();

        System.out.println(resp);
    }


}
