package com.dinuka.ryanair.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.dinuka.ryanair.model.Availability;
import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.service.AvailabilityService;
import com.dinuka.ryanair.service.DirectFlightLegService;
import com.dinuka.ryanair.service.InterConnectedFlightLegService;
import com.dinuka.ryanair.service.RouteService;
import com.dinuka.ryanair.util.DateTimeHelper;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

  private final DirectFlightLegService directFlightLegService;
  private final RouteService routeService;
  private final InterConnectedFlightLegService interConnectedFlightLegService;

  @Override
  public List<Availability> getAvailability(
      final FlightAvailabilityRequest flightAvailabilityRequest) {
    final RyanairDate arrivalDate =
        DateTimeHelper.getDate(flightAvailabilityRequest.getArrivalTime());
    final RyanairDate departureDate =
        DateTimeHelper.getDate(flightAvailabilityRequest.getDepartureTime());
    final List<Availability> availabilities =
        new ArrayList<>(
            getDirectFlightAvailabilities(arrivalDate, flightAvailabilityRequest, departureDate));

    availabilities.addAll(
        getInterConnectedFlightAvailabilities(
            arrivalDate, flightAvailabilityRequest, departureDate));
    return availabilities;
  }

  private List<Availability> getDirectFlightAvailabilities(
      final RyanairDate arrivalDate,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate) {

    final List<Availability> availabilities = new ArrayList<>();
    availabilities.add(
        Availability.builder()
            .stops(0)
            .legs(
                directFlightLegService.getFlightLegs(
                    arrivalDate, flightAvailabilityRequest, departureDate))
            .build());

    return availabilities;
  }

  private List<Availability> getInterConnectedFlightAvailabilities(
      final RyanairDate arrivalDate,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate) {

    final Set<String> interconnectedFlightRoutes =
        routeService.getInterconnectedFlightRoutes(flightAvailabilityRequest);

    final List<Availability> availabilities = new ArrayList<>();
    availabilities.add(
        Availability.builder()
            .stops(1)
            .legs(
                interConnectedFlightLegService.getInterconnectedFlightLegs(
                    arrivalDate,
                    flightAvailabilityRequest,
                    departureDate,
                    interconnectedFlightRoutes))
            .build());

    return availabilities;
  }
}
