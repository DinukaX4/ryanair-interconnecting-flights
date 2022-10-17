package com.dinuka.ryanair.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Leg {

  private final String departureAirport;
  private final String arrivalAirport;
  private final String departureDateTime;
  private final String arrivalDateTime;
}
