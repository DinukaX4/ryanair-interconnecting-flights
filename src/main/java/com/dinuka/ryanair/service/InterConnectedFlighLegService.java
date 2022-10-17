package com.dinuka.ryanair.service;

import java.util.List;
import java.util.Set;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

public interface InterConnectedFlighLegService {

  List<Leg> getInterconnectedFlightLegs(
      final String arrivalAirport,
      final RyanairDate arrivalDate,
      final String departureAirPort,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate,
      final Set<String> stops);
}
