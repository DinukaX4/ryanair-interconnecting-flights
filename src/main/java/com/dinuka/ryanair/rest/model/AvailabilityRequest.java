package com.dinuka.ryanair.rest.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
public class AvailabilityRequest {
  private final String arrivalAirPort;
  private final String departureAirPort;
  private final String year;
  private final String month;
}
