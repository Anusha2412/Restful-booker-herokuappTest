package com.app.qa.restfulbooker.data;

import com.app.qa.restfulbooker.model.Booking;
import com.app.qa.restfulbooker.model.BookingDates;
import net.datafaker.Faker;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BookingFactory {
    private static final Faker FAKER = new Faker();
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String[] NEEDS = {"Breakfast", "Lunch", "Dinner", "Late checkout", "Extra bed"};

    private BookingFactory() {
    }

    public static Booking randomBooking() {
        LocalDate checkin  = LocalDate.now().plusDays(FAKER.number().numberBetween(1, 30));
        LocalDate checkout = checkin.plusDays(FAKER.number().numberBetween(1, 14));
        return Booking.builder()
                .firstname(FAKER.name().firstName())
                .lastname(FAKER.name().lastName())
                .totalprice(FAKER.number().numberBetween(50, 1000))
                .depositpaid(FAKER.bool().bool())
                .bookingdates(BookingDates.builder()
                        .checkin(checkin.format(ISO))
                        .checkout(checkout.format(ISO))
                        .build())
                .additionalneeds(NEEDS[FAKER.number().numberBetween(0, NEEDS.length)])
                .build();
    }

    public static Map<String, Object> randomPartialUpdate() {
        Map<String, Object> patch = new LinkedHashMap<>();
        patch.put("firstname", FAKER.name().firstName());
        patch.put("lastname",  FAKER.name().lastName());
        return patch;
    }
}
