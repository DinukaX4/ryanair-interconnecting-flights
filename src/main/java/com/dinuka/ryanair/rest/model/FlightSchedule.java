package com.dinuka.ryanair.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonPropertyOrder({"day", "flights"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class FlightSchedule {
  @JsonProperty("day")
  private String day;

  @JsonProperty("flights")
  private List<Flight> flights;
}
