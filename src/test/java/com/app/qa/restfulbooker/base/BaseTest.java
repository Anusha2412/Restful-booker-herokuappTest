package com.app.qa.restfulbooker.base;

import com.app.qa.restfulbooker.config.ConfigLoader;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    protected String token;

    @BeforeSuite(alwaysRun = true)
    public void configureRestAssured() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", ConfigLoader.connectTimeoutMs())
                        .setParam("http.socket.timeout", ConfigLoader.readTimeoutMs()));
    }
}
