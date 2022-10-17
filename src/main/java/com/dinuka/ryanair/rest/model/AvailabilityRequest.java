package com.dinuka.ryanair.rest.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AvailabilityRequest {

  private final String arrivalAirPort;
  private final String departureAirPort;
  private final String year;
  private final String month;
}
