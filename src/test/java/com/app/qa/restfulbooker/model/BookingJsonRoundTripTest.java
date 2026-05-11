package com.app.qa.restfulbooker.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingJsonRoundTripTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldRoundTripBooking() throws Exception {
        Booking original = Booking.builder()
                .firstname("Jim").lastname("Brown")
                .totalprice(111).depositpaid(true)
                .bookingdates(BookingDates.builder().checkin("2026-01-01").checkout("2026-01-05").build())
                .additionalneeds("Breakfast")
                .build();

        String json = mapper.writeValueAsString(original);
        Booking restored = mapper.readValue(json, Booking.class);

        assertThat(restored).usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    public void shouldDeserializeRealBookerCreateResponse() throws Exception {
        String real = """
                {"bookingid":1,"booking":{"firstname":"Jim","lastname":"Brown",
                 "totalprice":111,"depositpaid":true,
                 "bookingdates":{"checkin":"2018-01-01","checkout":"2019-01-01"},
                 "additionalneeds":"Breakfast"}}
                """;

        CreateBookingResponse resp = mapper.readValue(real, CreateBookingResponse.class);

        assertThat(resp.getBookingid()).isEqualTo(1);
        assertThat(resp.getBooking().getFirstname()).isEqualTo("Jim");
        assertThat(resp.getBooking().getBookingdates().getCheckin()).isEqualTo("2018-01-01");
    }
}
