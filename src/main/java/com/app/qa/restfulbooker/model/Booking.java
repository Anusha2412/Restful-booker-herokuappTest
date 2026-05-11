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
public class Booking {
    @JsonProperty("firstname")       private String  firstname;
    @JsonProperty("lastname")        private String  lastname;
    @JsonProperty("totalprice")      private Integer totalprice;
    @JsonProperty("depositpaid")     private Boolean depositpaid;
    @JsonProperty("bookingdates")    private BookingDates bookingdates;
    @JsonProperty("additionalneeds") private String  additionalneeds;
}
