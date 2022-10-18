package com.dinuka.ryanair.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Route {
  @JsonProperty("airportFrom")
  private String airportFrom;

  @JsonProperty("airportTo")
  private String airportTo;

  @JsonProperty("connectingAirport")
  private String connectingAirPort;

  @JsonProperty("newRoute")
  private boolean newRoute;

  @JsonProperty("seasonalRoute")
  private boolean seasonal;

  @JsonProperty("operator")
  private String operator;

  @JsonProperty("group")
  private String group;

  @JsonProperty("similarArrivalAirportCodes")
  private List<String> similarArrivalAirportCodes;
}
