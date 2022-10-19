package com.dinuka.ryanair.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.service.DirectFlightLegService;
import com.dinuka.ryanair.service.InterConnectedFlightLegService;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterConnectedFlightLegServiceImpl implements InterConnectedFlightLegService {

  private final DirectFlightLegService directFlightLegService;

  @Override
  public List<Leg> getInterconnectedFlightLegs(
      final RyanairDate arrivalDate,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate,
      final Set<String> stops) {

    final List<Leg> legs = new ArrayList<>();
    stops.stream()
        .forEach(
            stop -> {
              final List<Leg> first =
                  directFlightLegService.getFlightLegs(
                      arrivalDate,
                      flightAvailabilityRequest.toBuilder().arrivalAirPort(stop).build(),
                      departureDate);

              final Predicate<List<Leg>> predicate = CollectionUtils::isEmpty;
              if (predicate.test(first)) {
                return;
              }
              final List<Leg> second =
                  directFlightLegService.getFlightLegs(
                      arrivalDate,
                      flightAvailabilityRequest.toBuilder().departureAirPort(stop).build(),
                      departureDate);

              if (predicate.test(second)) {
                return;
              }
              getAvailableFlightLegs(legs, first, second);
            });

    return legs;
  }

  private void getAvailableFlightLegs(
      final List<Leg> legs, final List<Leg> first, final List<Leg> second) {

    for (final Leg firstLeg : first) {
      boolean isFirstLegEligible = false;
      for (final Leg secondLeg : second) {
        if (Duration.between(
                    LocalDateTime.parse(firstLeg.getArrivalDateTime()),
                    LocalDateTime.parse(secondLeg.getDepartureDateTime()))
                .getSeconds()
            >= 7200) {
          isFirstLegEligible = true;
          legs.add(secondLeg);
        }
      }
      if (isFirstLegEligible) {
        legs.add(firstLeg);
      }
    }
  }
}
