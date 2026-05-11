package com.app.qa.restfulbooker.data;

import com.app.qa.restfulbooker.model.Booking;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingFactoryTest {

    @Test
    public void randomBookingIsFullyPopulated() {
        Booking b = BookingFactory.randomBooking();

        assertThat(b.getFirstname()).isNotBlank();
        assertThat(b.getLastname()).isNotBlank();
        assertThat(b.getTotalprice()).isPositive();
        assertThat(b.getDepositpaid()).isNotNull();
        assertThat(b.getAdditionalneeds()).isNotBlank();
        assertThat(b.getBookingdates()).isNotNull();
        assertThat(b.getBookingdates().getCheckin()).matches("\\d{4}-\\d{2}-\\d{2}");
        assertThat(b.getBookingdates().getCheckout()).matches("\\d{4}-\\d{2}-\\d{2}");
        assertThat(LocalDate.parse(b.getBookingdates().getCheckin()))
                .isBeforeOrEqualTo(LocalDate.parse(b.getBookingdates().getCheckout()));
    }

    @Test
    public void successiveCallsProduceDifferentData() {
        Booking a = BookingFactory.randomBooking();
        Booking b = BookingFactory.randomBooking();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    public void randomPartialUpdateContainsExpectedKeys() {
        Map<String, Object> patch = BookingFactory.randomPartialUpdate();
        assertThat(patch).containsOnlyKeys("firstname", "lastname");
        assertThat(patch.get("firstname")).asString().isNotBlank();
        assertThat(patch.get("lastname")).asString().isNotBlank();
    }
}
