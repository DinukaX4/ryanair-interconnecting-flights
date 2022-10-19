package com.dinuka.ryanair.service;

import java.util.Set;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;

public interface RouteService {

  /**
   * Provide the availability stops for the given arrival and departure airports
   *
   * @param flightAvailabilityRequest includes departure airport and arrival airport
   * @return include all available stops
   */
  Set<String> getInterconnectedFlightRoutes(FlightAvailabilityRequest flightAvailabilityRequest);
}
