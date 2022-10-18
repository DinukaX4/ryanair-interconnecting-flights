package com.dinuka.ryanair.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.rest.client.RyanairRestClient;
import com.dinuka.ryanair.rest.model.Route;
import com.dinuka.ryanair.service.RouteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
  private final RyanairRestClient restClient;

  @Override
  public Set<String> getInterconnectedFlightRoutes(
      final FlightAvailabilityRequest flightAvailabilityRequest) {

    final List<Route> availableRoutes = restClient.getAvailableRoutes();
    final List<String> arrivalStops =
        availableRoutes.stream()
            .filter(
                route -> route.getAirportTo().equals(flightAvailabilityRequest.getArrivalAirPort()))
            .map(Route::getAirportFrom)
            .collect(Collectors.toList());

    return CollectionUtils.isEmpty(arrivalStops)
        ? new HashSet<>()
        : availableRoutes.stream()
            .filter(
                route ->
                    route.getAirportFrom().equals(flightAvailabilityRequest.getDepartureAirPort())
                        && arrivalStops.contains(route.getAirportTo()))
            .map(Route::getAirportTo)
            .collect(Collectors.toSet());
  }
}
