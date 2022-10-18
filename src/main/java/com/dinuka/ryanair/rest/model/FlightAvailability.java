package com.dinuka.ryanair.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonPropertyOrder({"month", "days"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class FlightAvailability {
  @JsonProperty("month")
  private String month;

  @JsonProperty("days")
  private List<FlightSchedule> flightSchedules;
}
