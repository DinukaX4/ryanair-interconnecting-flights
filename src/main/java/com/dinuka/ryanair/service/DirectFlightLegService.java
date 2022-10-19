package com.dinuka.ryanair.service;

import java.util.List;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

public interface DirectFlightLegService {

  /**
   * Return available direct flight legs
   *
   * @param arrivalDate arrival date
   * @param flightAvailabilityRequest request
   * @param departureDate departure date
   * @return legs
   */
  List<Leg> getFlightLegs(
      final RyanairDate arrivalDate,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate);
}
