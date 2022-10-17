package com.dinuka.ryanair.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Objects;

import lombok.Data;

@JsonPropertyOrder({"month", "days"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class FlightAvailability {

  @JsonProperty("month")
  private String month;

  @JsonProperty("days")
  private List<FlightSchedule> flightSchedules;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final FlightAvailability that = (FlightAvailability) o;
    return Objects.equal(month, that.month) && Objects.equal(flightSchedules, that.flightSchedules);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(month, flightSchedules);
  }
}
