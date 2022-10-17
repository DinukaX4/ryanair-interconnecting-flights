package com.dinuka.ryanair.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.dinuka.ryanair.model.Availability;
import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.service.AvailabilityService;
import com.dinuka.ryanair.service.FlightLegService;
import com.dinuka.ryanair.service.InterConnectedFlighLegService;
import com.dinuka.ryanair.service.RouteService;
import com.dinuka.ryanair.util.DateTimeHelper;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

  private final FlightLegService flightLegService;
  private final RouteService routeService;
  private final InterConnectedFlighLegService interConnectedFlighLegService;

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
                flightLegService.getFlightLegs(
                    arrivalDate, flightAvailabilityRequest, departureDate))
            .build());

    return availabilities;
  }

  private List<Availability> getInterConnectedFlightAvailabilities(
      final RyanairDate arrivalDate,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate) {

    final Set<String> interconnectedFlightRoutes =
        routeService.getInterconnectedFlightRoutes(
            arrivalDate, flightAvailabilityRequest, departureDate);

    final List<Availability> availabilities = new ArrayList<>();
    availabilities.add(
        Availability.builder()
            .stops(1)
            .legs(
                interConnectedFlighLegService.getInterconnectedFlightLegs(
                    flightAvailabilityRequest.getArrivalAirPort(),
                    arrivalDate,
                    flightAvailabilityRequest.getDepartureAirPort(),
                    flightAvailabilityRequest,
                    departureDate,
                    interconnectedFlightRoutes))
            .build());

    return availabilities;
  }
}
