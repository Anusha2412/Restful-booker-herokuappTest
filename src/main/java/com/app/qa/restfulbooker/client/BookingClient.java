package com.app.qa.restfulbooker.client;

import com.app.qa.restfulbooker.config.ConfigLoader;
import com.app.qa.restfulbooker.config.Endpoints;
import com.app.qa.restfulbooker.model.AuthRequest;
import com.app.qa.restfulbooker.model.AuthResponse;
import com.app.qa.restfulbooker.model.Booking;
import com.app.qa.restfulbooker.model.CreateBookingResponse;
import com.app.qa.restfulbooker.spec.RequestSpecs;
import com.app.qa.restfulbooker.spec.ResponseSpecs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class BookingClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private BookingClient() {}

    @Step("Authenticate and obtain token")
    public static String getToken() {
        AuthRequest creds = AuthRequest.builder()
                .username(ConfigLoader.username())
                .password(ConfigLoader.password())
                .build();

        Response resp = given().spec(RequestSpecs.defaultSpec())
                .body(creds)
                .when().post(Endpoints.AUTH)
                .then().spec(ResponseSpecs.ok200Json())
                .extract().response();

        AuthResponse parsed = parse(resp, AuthResponse.class);
        if (parsed.getToken() == null || parsed.getToken().isBlank()) {
            throw new AssertionError("Auth succeeded but token was empty: " + resp.asString());
        }
        return parsed.getToken();
    }

    @Step("Create booking")
    public static CreateBookingResponse create(Booking booking) {
        Response resp = given().spec(RequestSpecs.defaultSpec())
                .body(booking)
                .when().post(Endpoints.BOOKING)
                .then().spec(ResponseSpecs.created200())
                .extract().response();
        return parse(resp, CreateBookingResponse.class);
    }

    @Step("Get booking by id: {id}")
    public static Booking getById(int id) {
        Response resp = given().spec(RequestSpecs.defaultSpec())
                .pathParam("id", id)
                .when().get(Endpoints.BOOKING_BY_ID)
                .then().spec(ResponseSpecs.ok200Json())
                .extract().response();
        return parse(resp, Booking.class);
    }

    @Step("Get raw booking response by id: {id}")
    public static Response getByIdRaw(int id) {
        return given().spec(RequestSpecs.defaultSpec())
                .pathParam("id", id)
                .when().get(Endpoints.BOOKING_BY_ID)
                .then().extract().response();
    }

    @Step("Full update (PUT) booking id: {id}")
    public static Booking update(int id, Booking booking, String token) {
        Response resp = given().spec(RequestSpecs.authSpec(token))
                .pathParam("id", id)
                .body(booking)
                .when().put(Endpoints.BOOKING_BY_ID)
                .then().spec(ResponseSpecs.updated200())
                .extract().response();
        return parse(resp, Booking.class);
    }

    @Step("Partial update (PATCH) booking id: {id}")
    public static Booking patch(int id, Map<String, Object> partial, String token) {
        Response resp = given().spec(RequestSpecs.authSpec(token))
                .pathParam("id", id)
                .body(partial)
                .when().patch(Endpoints.BOOKING_BY_ID)
                .then().spec(ResponseSpecs.updated200())
                .extract().response();
        return parse(resp, Booking.class);
    }

    @Step("Delete booking id: {id}")
    public static Response delete(int id, String token) {
        return given().spec(RequestSpecs.authSpec(token))
                .pathParam("id", id)
                .when().delete(Endpoints.BOOKING_BY_ID)
                .then().spec(ResponseSpecs.deleted201())
                .extract().response();
    }

    @Step("List all booking ids")
    public static List<Integer> listIds() {
        Response resp = given().spec(RequestSpecs.defaultSpec())
                .when().get(Endpoints.BOOKING)
                .then().spec(ResponseSpecs.ok200Json())
                .extract().response();
        return resp.jsonPath().getList("bookingid", Integer.class);
    }

    private static <T> T parse(Response resp, Class<T> type) {
        try {
            return MAPPER.readValue(resp.asString(), type);
        } catch (JsonProcessingException e) {
            throw new AssertionError(
                    "Could not parse " + type.getSimpleName() + " response: " + resp.asString(), e);
        }
    }
}
