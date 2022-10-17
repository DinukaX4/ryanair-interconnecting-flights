package com.dinuka.ryanair.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.dinuka.ryanair.exception.ValidateException;
import com.dinuka.ryanair.model.FlightAvailabilityRequest;

import lombok.SneakyThrows;

class ValidatorTest {

  @InjectMocks private Validator mockValidator;

  @SneakyThrows
  @BeforeEach
  void setup() {
    openMocks(this).close();
  }

  @Test
  void validateRequest_Failed_Arrival() {
    final Exception exception =
        assertThrows(
            ValidateException.class,
            () ->
                mockValidator.validateRequest(
                    FlightAvailabilityRequest.builder()
                        .arrivalAirPort(null)
                        .departureAirPort("WRO")
                        .departureTime("2022-11-01T07:00")
                        .arrivalTime("2022-11-01T18:50")
                        .build()));
    assertEquals("{INVALID_ARRIVAL=Arrival is required}", exception.getMessage());
  }

  @Test
  void validateRequest_Failed_Departure() {
    final Exception exception =
        assertThrows(
            ValidateException.class,
            () ->
                mockValidator.validateRequest(
                    FlightAvailabilityRequest.builder()
                        .arrivalAirPort("DUB")
                        .departureAirPort("")
                        .departureTime("2022-11-01T07:00")
                        .arrivalTime("2022-11-01T18:50")
                        .build()));
    assertEquals("{INVALID_DEPARTURE=Departure is required}", exception.getMessage());
  }

  @Test
  void validateRequest_Failed_Time() {
    final Exception exception =
        assertThrows(
            ValidateException.class,
            () ->
                mockValidator.validateRequest(
                    FlightAvailabilityRequest.builder()
                        .arrivalAirPort("DUB")
                        .departureAirPort("WRO")
                        .departureTime("2022--01T07:00")
                        .arrivalTime("2022-11-01T18:50")
                        .build()));
    assertEquals("{TIME_INVALID=2022--01T07:00}", exception.getMessage());
  }
}
