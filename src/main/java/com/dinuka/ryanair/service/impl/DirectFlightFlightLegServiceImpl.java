package com.dinuka.ryanair.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.rest.client.RyanairRestClient;
import com.dinuka.ryanair.rest.model.AvailabilityRequest;
import com.dinuka.ryanair.rest.model.AvailabilityRequest.AvailabilityRequestBuilder;
import com.dinuka.ryanair.rest.model.Flight;
import com.dinuka.ryanair.rest.model.FlightAvailability;
import com.dinuka.ryanair.rest.model.FlightSchedule;
import com.dinuka.ryanair.service.DirectFlightLegService;
import com.dinuka.ryanair.util.DateTimeHelper;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectFlightFlightLegServiceImpl implements DirectFlightLegService {

  private final RyanairRestClient restClient;

  @Override
  public List<Leg> getFlightLegs(
      final RyanairDate arrivalDate,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate) {

    final AvailabilityRequestBuilder availabilityRequestBuilder =
        AvailabilityRequest.builder()
            .arrivalAirPort(flightAvailabilityRequest.getArrivalAirPort())
            .departureAirPort(flightAvailabilityRequest.getDepartureAirPort());

    if (arrivalDate.getYear() > departureDate.getYear()
        || arrivalDate.getMonth() > departureDate.getMonth()) {
      return getDifferentYearsFlightLegs(arrivalDate, departureDate, availabilityRequestBuilder);
    } else {
      return getNormalLegs(
          arrivalDate, flightAvailabilityRequest, departureDate, availabilityRequestBuilder);
    }
  }

  private List<Leg> getNormalLegs(
      final RyanairDate arrivalDate,
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate,
      final AvailabilityRequestBuilder availabilityRequestBuilder) {

    final List<Leg> flights = new ArrayList<>();

    final FlightAvailability flightAvailability =
        getFlightAvailability(
            availabilityRequestBuilder
                .year(String.valueOf(departureDate.getYear()))
                .month(String.valueOf(departureDate.getMonth()))
                .build());

    if (getFlightAvailabilityPredicate().test(flightAvailability)) {
      flightAvailability.getFlightSchedules().stream()
          .forEach(
              flightSchedule ->
                  flights.addAll(
                      flightSchedule.getFlights().stream()
                          .filter(
                              normalFlightLegFilter(
                                  arrivalDate, departureDate, flightAvailability, flightSchedule))
                          .map(
                              buildNormalFlightLegs(
                                  flightAvailabilityRequest,
                                  departureDate,
                                  flightAvailability,
                                  flightSchedule))
                          .collect(Collectors.toList())));
    }
    return flights;
  }

  @NotNull
  private Predicate<FlightAvailability> getFlightAvailabilityPredicate() {
    return result -> result != null && !CollectionUtils.isEmpty(result.getFlightSchedules());
  }

  @NotNull
  private Function<Flight, Leg> buildNormalFlightLegs(
      final FlightAvailabilityRequest flightAvailabilityRequest,
      final RyanairDate departureDate,
      final FlightAvailability flightAvailability,
      final FlightSchedule flightSchedule) {
    return flight ->
        Leg.builder()
            .arrivalAirport(flightAvailabilityRequest.getArrivalAirPort())
            .departureAirport(flightAvailabilityRequest.getDepartureAirPort())
            .arrivalDateTime(
                DateTimeHelper.buildDate(
                    flight.getArrivalTime(),
                    DateTimeHelper.buildDate(
                        flight.getDepartureTime(),
                        departureDate.getYear(),
                        Integer.parseInt(flightAvailability.getMonth()),
                        Integer.parseInt(flightSchedule.getDay()))))
            .departureDateTime(
                DateTimeHelper.buildDate(
                    flight.getDepartureTime(),
                    DateTimeHelper.buildDate(
                        flight.getArrivalTime(),
                        departureDate.getYear(),
                        Integer.parseInt(flightAvailability.getMonth()),
                        Integer.parseInt(flightSchedule.getDay()))))
            .build();
  }

  private Predicate<Flight> normalFlightLegFilter(
      final RyanairDate arrivalDate,
      final RyanairDate departureDate,
      final FlightAvailability flightAvailability,
      final FlightSchedule flightSchedule) {

    return flight ->
        DateTimeHelper.isAfter(
                DateTimeHelper.buildDate(
                    flight.getDepartureTime(),
                    departureDate.getYear(),
                    Integer.parseInt(flightAvailability.getMonth()),
                    Integer.parseInt(flightSchedule.getDay())),
                departureDate.getLocalDateTime())
            && DateTimeHelper.isBefore(
                DateTimeHelper.buildDate(
                    flight.getArrivalTime(),
                    arrivalDate.getYear(),
                    arrivalDate.getMonth(),
                    Integer.parseInt(flightSchedule.getDay())),
                arrivalDate.getLocalDateTime());
  }

  // this method is to get flights if the departure and the arrival is belongs to different years
  private List<Leg> getDifferentYearsFlightLegs(
      final RyanairDate arrivalDate,
      final RyanairDate departureDate,
      final AvailabilityRequestBuilder availabilityRequestBuilder) {

    final AvailabilityRequest request = availabilityRequestBuilder.build();

    final Predicate<FlightAvailability> isResultAvailable = getFlightAvailabilityPredicate();
    final List<Leg> flights = new ArrayList<>();
    // get the departure year month availabilities
    final FlightAvailability departureFlightAvailability =
        getFlightAvailability(
            availabilityRequestBuilder
                .year(String.valueOf(departureDate.getYear()))
                .month(String.valueOf(departureDate.getMonth()))
                .build());

    // get the arrival year, month availabilities
    final FlightAvailability arrivalFlightAvailability =
        getFlightAvailability(
            availabilityRequestBuilder
                .year(String.valueOf(arrivalDate.getYear()))
                .month(String.valueOf(arrivalDate.getMonth()))
                .build());

    if (isResultAvailable.test(arrivalFlightAvailability)) {
      addFlightLegs(arrivalDate, departureDate, request, flights, arrivalFlightAvailability);
    }

    if (isResultAvailable.test(departureFlightAvailability)) {
      addFlightLegs(arrivalDate, departureDate, request, flights, departureFlightAvailability);
    }

    return flights;
  }

  private void addFlightLegs(
      final RyanairDate arrivalDate,
      final RyanairDate departureDate,
      final AvailabilityRequest request,
      final List<Leg> flights,
      final FlightAvailability flightAvailability) {

    flightAvailability.getFlightSchedules().stream()
        .forEach(
            flightSchedule ->
                flights.addAll(
                    flightSchedule.getFlights().stream()
                        .filter(
                            filterFlightLeg(
                                arrivalDate, departureDate, flightAvailability, flightSchedule))
                        .map(
                            buildFlightLegs(
                                arrivalDate, request, flightAvailability, flightSchedule))
                        .collect(Collectors.toList())));
  }

  private Function<Flight, Leg> buildFlightLegs(
      final RyanairDate arrivalDate,
      final AvailabilityRequest request,
      final FlightAvailability flightAvailability,
      final FlightSchedule flightSchedule) {

    return flight ->
        Leg.builder()
            .arrivalAirport(request.getArrivalAirPort())
            .departureAirport(request.getDepartureAirPort())
            .arrivalDateTime(
                DateTimeHelper.buildDate(
                    flight.getArrivalTime(),
                    DateTimeHelper.buildDate(
                        flight.getArrivalTime(),
                        arrivalDate.getYear(),
                        Integer.parseInt(flightAvailability.getMonth()),
                        Integer.parseInt(flightSchedule.getDay()))))
            .departureDateTime(
                DateTimeHelper.buildDate(
                    flight.getDepartureTime(),
                    DateTimeHelper.buildDate(
                        flight.getArrivalTime(),
                        arrivalDate.getYear(),
                        Integer.parseInt(flightAvailability.getMonth()),
                        Integer.parseInt(flightSchedule.getDay()))))
            .build();
  }

  private Predicate<Flight> filterFlightLeg(
      final RyanairDate arrivalDate,
      final RyanairDate departureDate,
      final FlightAvailability flightAvailability,
      final FlightSchedule flightSchedule) {

    return flight ->
        DateTimeHelper.isAfter(
                DateTimeHelper.buildDate(
                    flight.getDepartureTime(),
                    arrivalDate.getYear(),
                    Integer.parseInt(flightAvailability.getMonth()),
                    Integer.parseInt(flightSchedule.getDay())),
                departureDate.getLocalDateTime())
            && DateTimeHelper.isBefore(
                DateTimeHelper.buildDate(
                    flight.getArrivalTime(),
                    arrivalDate.getYear(),
                    Integer.parseInt(flightAvailability.getMonth()),
                    Integer.parseInt(flightSchedule.getDay())),
                arrivalDate.getLocalDateTime());
  }

  private FlightAvailability getFlightAvailability(final AvailabilityRequest availabilityRequest) {
    return restClient.getFlightAvailability(availabilityRequest);
  }
}
