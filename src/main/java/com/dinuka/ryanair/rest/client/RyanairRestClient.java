package com.dinuka.ryanair.rest.client;

import java.util.List;

import com.dinuka.ryanair.rest.model.AvailabilityRequest;
import com.dinuka.ryanair.rest.model.FlightAvailability;
import com.dinuka.ryanair.rest.model.Route;

public interface RyanairRestClient {

  /**
   * Provide the Ryanair Route response
   *
   * @return response
   */
  List<Route> getAvailableRoutes();

  /**
   * Provide the Flight schedules for the given request
   *
   * @param request request includes arrival airport departure airport schedule requested year
   *     schedule requested month
   * @return flight schedules
   */
  FlightAvailability getFlightAvailability(AvailabilityRequest request);
}
