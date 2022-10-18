package com.dinuka.ryanair.service;

import java.util.List;
import java.util.Set;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

public interface InterConnectedFlightLegService {

  List<Leg> getInterconnectedFlightLegs(
      final RyanairDate arrivalDate,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate,
      final Set<String> stops);
}
