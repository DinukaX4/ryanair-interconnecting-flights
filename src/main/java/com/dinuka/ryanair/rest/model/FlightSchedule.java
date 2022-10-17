package com.dinuka.ryanair.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Objects;

import lombok.Data;

@JsonPropertyOrder({"day", "flights"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class FlightSchedule {

  @JsonProperty("day")
  private String day;

  @JsonProperty("flights")
  private List<Flight> flights;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final FlightSchedule that = (FlightSchedule) o;
    return Objects.equal(day, that.day) && Objects.equal(flights, that.flights);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(day, flights);
  }
}
