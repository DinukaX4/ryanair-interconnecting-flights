package com.dinuka.ryanair.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public class FlightAvailabilityRequest {

  private final String arrivalAirPort;
  private final String departureAirPort;
  private final String arrivalTime;
  private final String departureTime;
}
