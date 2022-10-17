package com.dinuka.ryanair.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.rest.client.RyanairRestClient;
import com.dinuka.ryanair.service.FlightLegService;
import com.dinuka.ryanair.service.InterConnectedFlighLegService;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterConnectedFlightLegServiceImpl implements InterConnectedFlighLegService {

  private final RyanairRestClient restClient;
  private final FlightLegService flightLegService;

  @Override
  public List<Leg> getInterconnectedFlightLegs(
      final String arrivalAirport,
      final RyanairDate arrivalDate,
      final String departureAirPort,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate,
      final Set<String> stops) {

    final List<Leg> legs = new ArrayList<>();
    stops.stream()
        .forEach(
            stop -> {
              final List<Leg> first =
                  flightLegService.getFlightLegs(
                      arrivalDate,
                      flightAvailabilityRequest.toBuilder().arrivalAirPort(stop).build(),
                      departureDate);

              if (CollectionUtils.isEmpty(first)) {
                return;
              }
              final List<Leg> second =
                  flightLegService.getFlightLegs(
                      arrivalDate,
                      flightAvailabilityRequest.toBuilder().departureAirPort(stop).build(),
                      departureDate);
              if (CollectionUtils.isEmpty(second)) {
                return;
              }

              for (final Leg firstLeg : first) {
                boolean isFirstLegEligible = false;
                for (final Leg secondLeg : second) {

                  if (Duration.between(
                              LocalDateTime.parse(firstLeg.getArrivalDateTime()),
                              LocalDateTime.parse(secondLeg.getDepartureDateTime()))
                          .getSeconds()
                      >= 3600) {
                    isFirstLegEligible = true;
                    legs.add(secondLeg);
                  }
                }
                if (isFirstLegEligible) {
                  legs.add(firstLeg);
                }
              }
            });

    return legs;
  }
}
