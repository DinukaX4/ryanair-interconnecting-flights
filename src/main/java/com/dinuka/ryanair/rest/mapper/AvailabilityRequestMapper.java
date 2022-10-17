package com.dinuka.ryanair.rest.mapper;

import org.springframework.stereotype.Component;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;

@Component
public class AvailabilityRequestMapper {

  public FlightAvailabilityRequest mapToServer(
      final String departure,
      final String arrival,
      final String departureTime,
      final String arrivalTime) {

    return FlightAvailabilityRequest.builder()
        .departureAirPort(departure)
        .departureTime(departureTime)
        .arrivalAirPort(arrival)
        .arrivalTime(arrivalTime)
        .build();
  }
}
