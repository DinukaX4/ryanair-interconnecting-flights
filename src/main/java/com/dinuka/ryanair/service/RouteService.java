package com.dinuka.ryanair.service;

import java.util.Set;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;

/** Provide Route Services */
public interface RouteService {

  Set<String> getInterconnectedFlightRoutes(FlightAvailabilityRequest flightAvailabilityRequest);
}
