package com.app.qa.restfulbooker.spec;

import com.app.qa.restfulbooker.config.ConfigLoader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class RequestSpecs {
    private RequestSpecs() {}

    public static RequestSpecification defaultSpec() {
        // NOTE: setAccept(ContentType.JSON) expands to "application/json, application/javascript,
        // text/javascript, text/json". Restful-booker has a teapot easter egg that returns
        // 418 when "text/json" appears in the Accept header — so we set it as a literal string.
        return new RequestSpecBuilder()
                .setBaseUri(ConfigLoader.baseUrl())
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .addFilter(new AllureRestAssured())
                .build();
    }

    public static RequestSpecification authSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(defaultSpec())
                .addCookie("token", token)
                .build();
    }
}
