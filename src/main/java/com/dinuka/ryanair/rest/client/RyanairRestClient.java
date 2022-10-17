package com.dinuka.ryanair.rest.client;

import java.util.List;

import com.dinuka.ryanair.rest.model.AvailabilityRequest;
import com.dinuka.ryanair.rest.model.FlightAvailability;
import com.dinuka.ryanair.rest.model.Route;

public interface RyanairRestClient {

  List<Route> getAvailableRoutes();

  FlightAvailability getFlightAvailability(AvailabilityRequest request);
}
