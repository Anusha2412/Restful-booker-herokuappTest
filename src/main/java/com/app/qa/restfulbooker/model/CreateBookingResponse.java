package com.app.qa.restfulbooker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponse {
    @JsonProperty("bookingid") private Integer bookingid;
    @JsonProperty("booking")   private Booking booking;
}
