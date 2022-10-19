package com.dinuka.ryanair.service;

import java.util.List;

import com.dinuka.ryanair.model.Availability;
import com.dinuka.ryanair.model.FlightAvailabilityRequest;

public interface AvailabilityService {

  /**
   * Give the all availabilities for the given request
   *
   * @param flightAvailabilityRequest include the request parameters arrivalAirPort, departureAirPor
   *     arrivalTime departureTime
   * @return include the results upto maximum two stops
   */
  List<Availability> getAvailability(final FlightAvailabilityRequest flightAvailabilityRequest);
}
