package com.dinuka.ryanair.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.rest.client.RyanairRestClient;
import com.dinuka.ryanair.rest.model.AvailabilityRequest;
import com.dinuka.ryanair.rest.model.AvailabilityRequest.AvailabilityRequestBuilder;
import com.dinuka.ryanair.rest.model.FlightAvailability;
import com.dinuka.ryanair.service.FlightLegService;
import com.dinuka.ryanair.util.DateTimeHelper;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectFlightFlightLegServiceImpl implements FlightLegService {

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

      return getDifferentYearsLegs(arrivalDate, departureDate, availabilityRequestBuilder);
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

    flightAvailability.getFlightSchedules().stream()
        .forEach(
            flightSchedule ->
                flights.addAll(
                    flightSchedule.getFlights().stream()
                        .filter(
                            flight ->
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
                                        arrivalDate.getLocalDateTime()))
                        .map(
                            flight ->
                                Leg.builder()
                                    .arrivalAirport(flightAvailabilityRequest.getArrivalAirPort())
                                    .departureAirport(
                                        flightAvailabilityRequest.getDepartureAirPort())
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
                                    .build())
                        .collect(Collectors.toList())));
    return flights;
  }

  // this method is to get flights if the departure and the arrival is belongs to different years
  private List<Leg> getDifferentYearsLegs(
      final RyanairDate arrivalDate,
      final RyanairDate departureDate,
      final AvailabilityRequestBuilder availabilityRequestBuilder) {

    final AvailabilityRequest request = availabilityRequestBuilder.build();

    final List<Leg> flights = new ArrayList<>();
    final FlightAvailability departureFlightAvailability =
        getFlightAvailability(
            availabilityRequestBuilder
                .year(String.valueOf(departureDate.getYear()))
                .month(String.valueOf(departureDate.getMonth()))
                .build());

    final FlightAvailability arrivalFlightAvailability =
        getFlightAvailability(
            availabilityRequestBuilder
                .year(String.valueOf(arrivalDate.getYear()))
                .month(String.valueOf(arrivalDate.getMonth()))
                .build());

    arrivalFlightLegs(arrivalDate, departureDate, request, flights, arrivalFlightAvailability);

    departureFlightLegs(arrivalDate, departureDate, request, flights, departureFlightAvailability);

    return flights;
  }

  private void departureFlightLegs(
      final RyanairDate arrivalDate,
      final RyanairDate departureDate,
      final AvailabilityRequest request,
      final List<Leg> flights,
      final FlightAvailability departureFlightAvailability) {

    departureFlightAvailability.getFlightSchedules().stream()
        .forEach(
            flightSchedule ->
                flights.addAll(
                    flightSchedule.getFlights().stream()
                        .filter(
                            flight ->
                                DateTimeHelper.isAfter(
                                        DateTimeHelper.buildDate(
                                            flight.getDepartureTime(),
                                            departureDate.getYear(),
                                            Integer.parseInt(
                                                departureFlightAvailability.getMonth()),
                                            Integer.parseInt(flightSchedule.getDay())),
                                        departureDate.getLocalDateTime())
                                    && DateTimeHelper.isBefore(
                                        DateTimeHelper.buildDate(
                                            flight.getArrivalTime(),
                                            departureDate.getYear(),
                                            Integer.parseInt(
                                                departureFlightAvailability.getMonth()),
                                            Integer.parseInt(flightSchedule.getDay())),
                                        arrivalDate.getLocalDateTime()))
                        .map(
                            flight ->
                                Leg.builder()
                                    .arrivalAirport(request.getArrivalAirPort())
                                    .departureAirport(request.getDepartureAirPort())
                                    .arrivalDateTime(
                                        DateTimeHelper.buildDate(
                                            flight.getArrivalTime(),
                                            DateTimeHelper.buildDate(
                                                flight.getDepartureTime(),
                                                departureDate.getYear(),
                                                Integer.parseInt(
                                                    departureFlightAvailability.getMonth()),
                                                Integer.parseInt(flightSchedule.getDay()))))
                                    .departureDateTime(
                                        DateTimeHelper.buildDate(
                                            flight.getDepartureTime(),
                                            DateTimeHelper.buildDate(
                                                flight.getDepartureTime(),
                                                departureDate.getYear(),
                                                Integer.parseInt(
                                                    departureFlightAvailability.getMonth()),
                                                Integer.parseInt(flightSchedule.getDay()))))
                                    .build())
                        .collect(Collectors.toList())));
  }

  private void arrivalFlightLegs(
      final RyanairDate arrivalDate,
      final RyanairDate departureDate,
      final AvailabilityRequest request,
      final List<Leg> flights,
      final FlightAvailability arrivalFlightAvailability) {
    arrivalFlightAvailability.getFlightSchedules().stream()
        .forEach(
            flightSchedule ->
                flights.addAll(
                    flightSchedule.getFlights().stream()
                        .filter(
                            flight ->
                                DateTimeHelper.isAfter(
                                        DateTimeHelper.buildDate(
                                            flight.getDepartureTime(),
                                            arrivalDate.getYear(),
                                            Integer.parseInt(arrivalFlightAvailability.getMonth()),
                                            Integer.parseInt(flightSchedule.getDay())),
                                        departureDate.getLocalDateTime())
                                    && DateTimeHelper.isBefore(
                                        DateTimeHelper.buildDate(
                                            flight.getArrivalTime(),
                                            arrivalDate.getYear(),
                                            Integer.parseInt(arrivalFlightAvailability.getMonth()),
                                            Integer.parseInt(flightSchedule.getDay())),
                                        arrivalDate.getLocalDateTime()))
                        .map(
                            flight ->
                                Leg.builder()
                                    .arrivalAirport(request.getArrivalAirPort())
                                    .departureAirport(request.getDepartureAirPort())
                                    .arrivalDateTime(
                                        DateTimeHelper.buildDate(
                                            flight.getArrivalTime(),
                                            DateTimeHelper.buildDate(
                                                flight.getArrivalTime(),
                                                arrivalDate.getYear(),
                                                Integer.parseInt(
                                                    arrivalFlightAvailability.getMonth()),
                                                Integer.parseInt(flightSchedule.getDay()))))
                                    .departureDateTime(
                                        DateTimeHelper.buildDate(
                                            flight.getDepartureTime(),
                                            DateTimeHelper.buildDate(
                                                flight.getArrivalTime(),
                                                arrivalDate.getYear(),
                                                Integer.parseInt(
                                                    arrivalFlightAvailability.getMonth()),
                                                Integer.parseInt(flightSchedule.getDay()))))
                                    .build())
                        .collect(Collectors.toList())));
  }

  private FlightAvailability getFlightAvailability(final AvailabilityRequest availabilityRequest) {
    return restClient.getFlightAvailability(availabilityRequest);
  }
}
