package com.dinuka.ryanair.service;

import java.util.Set;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

public interface RouteService {

  Set<String> getInterconnectedFlightRoutes(
      RyanairDate arrivalDate,
      FlightAvailabilityRequest flightAvailabilityRequest,
      RyanairDate departureDate);
}
