package com.app.qa.restfulbooker.tests;

import com.app.qa.restfulbooker.base.BaseTest;
import com.app.qa.restfulbooker.client.BookingClient;
import com.app.qa.restfulbooker.data.BookingFactory;
import com.app.qa.restfulbooker.model.Booking;
import com.app.qa.restfulbooker.model.CreateBookingResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker")
@Feature("Booking CRUD Lifecycle")
public class BookingCrudLifecycleTest extends BaseTest {

    private int     bookingId;
    private Booking createdBooking;

    @Test(priority = 1)
    @Story("Authentication")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Authenticate and store the token used by mutation steps.")
    public void shouldAuthenticateAndStoreToken() {
        this.token = BookingClient.getToken();
        assertThat(this.token).as("auth token").isNotBlank();
    }

    @Test(priority = 2, dependsOnMethods = "shouldAuthenticateAndStoreToken")
    @Story("Create")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a booking with a randomly generated payload and validate the echoed body matches.")
    public void shouldCreateBooking() {
        Booking payload = BookingFactory.randomBooking();

        CreateBookingResponse resp = BookingClient.create(payload);

        assertThat(resp.getBookingid()).as("bookingid").isPositive();
        assertThat(resp.getBooking())
                .as("server-echoed booking matches payload")
                .usingRecursiveComparison()
                .isEqualTo(payload);

        this.bookingId      = resp.getBookingid();
        this.createdBooking = resp.getBooking();
    }

    @Test(priority = 3, dependsOnMethods = "shouldCreateBooking")
    @Story("Read - persistence after create")
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET the just-created booking and verify the server persisted exactly what was sent.")
    public void shouldReadCreatedBooking() {
        Booking fetched = BookingClient.getById(bookingId);

        assertThat(fetched)
                .as("fetched booking equals what was created")
                .usingRecursiveComparison()
                .isEqualTo(createdBooking);
    }

    @Test(priority = 4, dependsOnMethods = "shouldReadCreatedBooking")
    @Story("Update (PUT)")
    @Severity(SeverityLevel.NORMAL)
    @Description("Replace the booking entirely via PUT.")
    public void shouldUpdateBookingFully() {
        Booking updated = BookingFactory.randomBooking();

        Booking resp = BookingClient.update(bookingId, updated, token);

        assertThat(resp).usingRecursiveComparison().isEqualTo(updated);
        this.createdBooking = updated;
    }

    @Test(priority = 5, dependsOnMethods = "shouldUpdateBookingFully")
    @Story("Read - persistence after PUT")
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET after PUT and verify the full update persisted.")
    public void shouldReadAfterUpdate() {
        Booking fetched = BookingClient.getById(bookingId);
        assertThat(fetched).usingRecursiveComparison().isEqualTo(createdBooking);
    }

    @Test(priority = 6, dependsOnMethods = "shouldReadAfterUpdate")
    @Story("Patch")
    @Severity(SeverityLevel.NORMAL)
    @Description("Partially update only firstname and lastname via PATCH; untouched fields must remain unchanged.")
    public void shouldPatchBookingPartially() {
        Map<String, Object> patch = BookingFactory.randomPartialUpdate();

        Booking resp = BookingClient.patch(bookingId, patch, token);

        assertThat(resp.getFirstname()).isEqualTo(patch.get("firstname"));
        assertThat(resp.getLastname()).isEqualTo(patch.get("lastname"));
        assertThat(resp.getTotalprice()).isEqualTo(createdBooking.getTotalprice());
        assertThat(resp.getDepositpaid()).isEqualTo(createdBooking.getDepositpaid());
        assertThat(resp.getBookingdates()).usingRecursiveComparison().isEqualTo(createdBooking.getBookingdates());
        assertThat(resp.getAdditionalneeds()).isEqualTo(createdBooking.getAdditionalneeds());

        this.createdBooking.setFirstname((String) patch.get("firstname"));
        this.createdBooking.setLastname((String) patch.get("lastname"));
    }

    @Test(priority = 7, dependsOnMethods = "shouldPatchBookingPartially")
    @Story("Read - persistence after PATCH")
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET after PATCH and verify the partial update persisted while other fields stayed.")
    public void shouldReadAfterPatch() {
        Booking fetched = BookingClient.getById(bookingId);
        assertThat(fetched).usingRecursiveComparison().isEqualTo(createdBooking);
    }

    @Test(priority = 8, dependsOnMethods = "shouldReadAfterPatch")
    @Story("Delete")
    @Severity(SeverityLevel.CRITICAL)
    @Description("DELETE the booking. Restful-booker returns 201 (not 204).")
    public void shouldDeleteBooking() {
        Response resp = BookingClient.delete(bookingId, token);
        assertThat(resp.getStatusCode()).isEqualTo(201);
    }

    @Test(priority = 9, dependsOnMethods = "shouldDeleteBooking")
    @Story("Read - persistence after DELETE")
    @Severity(SeverityLevel.BLOCKER)
    @Description("GET after DELETE must return 404 to confirm the delete persisted.")
    public void shouldReturn404AfterDelete() {
        Response resp = BookingClient.getByIdRaw(bookingId);
        assertThat(resp.getStatusCode()).isEqualTo(404);
    }
}
