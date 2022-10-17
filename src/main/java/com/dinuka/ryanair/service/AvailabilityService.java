package com.dinuka.ryanair.service;

import java.util.List;

import com.dinuka.ryanair.model.Availability;
import com.dinuka.ryanair.model.FlightAvailabilityRequest;

public interface AvailabilityService {

  List<Availability> getAvailability(final FlightAvailabilityRequest flightAvailabilityRequest);
}
