package com.dinuka.ryanair.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.rest.client.RyanairRestClient;
import com.dinuka.ryanair.rest.model.Route;
import com.dinuka.ryanair.service.RouteService;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
  private final RyanairRestClient restClient;

  @Override
  public Set<String> getInterconnectedFlightRoutes(
      final RyanairDate arrivalDate,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate) {

    final List<Route> availableRoutes = restClient.getAvailableRoutes();
    final List<String> arrivalStops =
        availableRoutes.stream()
            .filter(route -> route.getTo().equals(flightAvailabilityRequest.getArrivalAirPort()))
            .map(Route::getFrom)
            .collect(Collectors.toList());

    return availableRoutes.stream()
        .filter(
            route ->
                route.getFrom().equals(flightAvailabilityRequest.getDepartureAirPort())
                    && arrivalStops.contains(route.getTo()))
        .map(Route::getTo)
        .collect(Collectors.toSet());
  }
}
