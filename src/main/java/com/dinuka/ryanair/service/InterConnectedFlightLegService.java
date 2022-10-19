package com.dinuka.ryanair.service;

import java.util.List;
import java.util.Set;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

public interface InterConnectedFlightLegService {

  /**
   * Give the legs of interconnected flight upto one stop
   *
   * @param arrivalDate arrival date
   * @param flightAvailabilityRequest request
   * @param departureDate departure date
   * @param stops interconnected airports
   * @return legs
   */
  List<Leg> getInterconnectedFlightLegs(
      final RyanairDate arrivalDate,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate,
      final Set<String> stops);
}
