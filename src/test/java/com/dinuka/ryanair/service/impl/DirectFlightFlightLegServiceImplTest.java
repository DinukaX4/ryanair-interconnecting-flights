package com.dinuka.ryanair.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.rest.client.RyanairRestClient;
import com.dinuka.ryanair.rest.model.AvailabilityRequest;
import com.dinuka.ryanair.rest.model.FlightAvailability;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

class DirectFlightFlightLegServiceImplTest {

  @InjectMocks DirectFlightFlightLegServiceImpl mockDirectFlightFlightLegService;
  @Mock RyanairRestClient mockRestClient;
  private Gson gson;

  @BeforeEach
  void setUp() throws Exception {
    openMocks(this).close();
    gson = new Gson();
  }

  @Test
  void getFlightLegs_same_year_same_month() throws FileNotFoundException {

    final FlightAvailability availability =
        gson.fromJson(
            new JsonReader(
                new FileReader(
                    Objects.requireNonNull(
                            this.getClass().getResource("/schedules-same-moth-year.json"))
                        .getFile())),
            new TypeToken<FlightAvailability>() {}.getType());

    when(mockRestClient.getFlightAvailability(any())).thenReturn(availability);

    final RyanairDate departureDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-01T13:00:30"))
            .day(1)
            .month(11)
            .year(2022)
            .build();

    final RyanairDate arrivalDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-02T23:00:30"))
            .day(1)
            .month(11)
            .year(2022)
            .build();

    final List<Leg> results =
        mockDirectFlightFlightLegService.getFlightLegs(
            arrivalDate,
            FlightAvailabilityRequest.builder()
                .arrivalTime("2022-11-02T23:00:30")
                .departureTime("2022-11-01T13:00:30")
                .departureAirPort("DUB")
                .arrivalAirPort("WRO")
                .build(),
            departureDate);

    assertNotNull(results);
    assertEquals(3, results.size());
    assertTrue(
        results.stream()
            .map(Leg::getArrivalDateTime)
            .collect(Collectors.toList())
            .contains("2022-11-02T19:45"));
    assertTrue(
        results.stream()
            .map(Leg::getDepartureDateTime)
            .collect(Collectors.toList())
            .contains("2022-11-02T16:15"));

    assertTrue(
        results.stream()
            .map(Leg::getArrivalDateTime)
            .collect(Collectors.toList())
            .contains("2022-11-01T16:50"));
    assertTrue(
        results.stream()
            .map(Leg::getDepartureDateTime)
            .collect(Collectors.toList())
            .contains("2022-11-01T13:20"));
  }

  @Test
  void getFlightLegs_same_year_same_month_equal_departure_time() throws FileNotFoundException {

    final FlightAvailability availability =
        gson.fromJson(
            new JsonReader(
                new FileReader(
                    Objects.requireNonNull(
                            this.getClass().getResource("/schedules-same-moth-year.json"))
                        .getFile())),
            new TypeToken<FlightAvailability>() {}.getType());

    when(mockRestClient.getFlightAvailability(any())).thenReturn(availability);

    final RyanairDate departureDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-02T21:20:00"))
            .day(1)
            .month(11)
            .year(2022)
            .build();

    final RyanairDate arrivalDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-02T23:50:00"))
            .day(1)
            .month(11)
            .year(2022)
            .build();

    final List<Leg> results =
        mockDirectFlightFlightLegService.getFlightLegs(
            arrivalDate,
            FlightAvailabilityRequest.builder()
                .arrivalTime("2022-11-02T23:50:30")
                .departureTime("2022-11-02T21:00:00")
                .departureAirPort("DUB")
                .arrivalAirPort("WRO")
                .build(),
            departureDate);
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(
        results.stream()
            .map(Leg::getArrivalDateTime)
            .collect(Collectors.toList())
            .contains("2022-11-02T23:50"));
    assertTrue(
        results.stream()
            .map(Leg::getDepartureDateTime)
            .collect(Collectors.toList())
            .contains("2022-11-02T21:20"));
  }

  @Test
  void getFlightLegs_same_year_same_month_equal_arrival_time() throws FileNotFoundException {

    final FlightAvailability availability =
        gson.fromJson(
            new JsonReader(
                new FileReader(
                    Objects.requireNonNull(
                            this.getClass().getResource("/schedules-same-moth-year.json"))
                        .getFile())),
            new TypeToken<FlightAvailability>() {}.getType());

    when(mockRestClient.getFlightAvailability(any())).thenReturn(availability);

    final RyanairDate departureDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-02T15:20:00"))
            .day(1)
            .month(11)
            .year(2022)
            .build();

    final RyanairDate arrivalDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-02T19:45:00"))
            .day(1)
            .month(11)
            .year(2022)
            .build();

    final List<Leg> results =
        mockDirectFlightFlightLegService.getFlightLegs(
            arrivalDate,
            FlightAvailabilityRequest.builder()
                .arrivalTime("2022-11-02T19:45:00")
                .departureTime("2022-11-02T15:20:00")
                .departureAirPort("DUB")
                .arrivalAirPort("WRO")
                .build(),
            departureDate);
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(
        results.stream()
            .map(Leg::getArrivalDateTime)
            .collect(Collectors.toList())
            .contains("2022-11-02T19:45"));
    assertTrue(
        results.stream()
            .map(Leg::getDepartureDateTime)
            .collect(Collectors.toList())
            .contains("2022-11-02T16:15"));
  }

  @Test
  void getFlightLegs_same_year_same_month_no_results() {

    when(mockRestClient.getFlightAvailability(any())).thenReturn(null);

    final RyanairDate departureDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-02T21:20:00"))
            .day(1)
            .month(11)
            .year(2022)
            .build();

    final RyanairDate arrivalDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-02T23:50:00"))
            .day(1)
            .month(11)
            .year(2022)
            .build();

    final List<Leg> results =
        mockDirectFlightFlightLegService.getFlightLegs(
            arrivalDate,
            FlightAvailabilityRequest.builder()
                .arrivalTime("2022-11-02T23:50:30")
                .departureTime("2022-11-02T21:00:00")
                .departureAirPort("DUB")
                .arrivalAirPort("WRO")
                .build(),
            departureDate);
    assertNotNull(results);
    assertEquals(0, results.size());
  }

  @Test
  void getFlightLegs_same_year_different_month() throws FileNotFoundException {

    final List<FlightAvailability> availabilities =
        gson.fromJson(
            new JsonReader(
                new FileReader(
                    Objects.requireNonNull(
                            this.getClass().getResource("/schedules-different-moth.json"))
                        .getFile())),
            new TypeToken<List<FlightAvailability>>() {}.getType());

    final Map<String, FlightAvailability> availabilityMap =
        availabilities.stream()
            .collect(Collectors.toMap(FlightAvailability::getMonth, Function.identity()));

    when(mockRestClient.getFlightAvailability(
            AvailabilityRequest.builder()
                .year("2022")
                .month("11")
                .departureAirPort("DUB")
                .arrivalAirPort("WRO")
                .build()))
        .thenReturn(availabilityMap.getOrDefault("11", new FlightAvailability()));

    when(mockRestClient.getFlightAvailability(
            AvailabilityRequest.builder()
                .year("2022")
                .month("12")
                .departureAirPort("DUB")
                .arrivalAirPort("WRO")
                .build()))
        .thenReturn(availabilityMap.getOrDefault("12", new FlightAvailability()));

    final RyanairDate departureDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-29T21:00:30"))
            .day(29)
            .month(11)
            .year(2022)
            .build();

    final RyanairDate arrivalDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-12-02T21:00:30"))
            .day(2)
            .month(12)
            .year(2022)
            .build();

    final List<Leg> results =
        mockDirectFlightFlightLegService.getFlightLegs(
            arrivalDate,
            FlightAvailabilityRequest.builder()
                .arrivalTime("2022-12-02T21:00:30")
                .departureTime("2022-11-29T21:00:30")
                .departureAirPort("DUB")
                .arrivalAirPort("WRO")
                .build(),
            departureDate);

    assertNotNull(results);
    assertEquals(5, results.size());
    assertTrue(
        results.stream()
            .map(Leg::getArrivalDateTime)
            .collect(Collectors.toList())
            .contains("2022-12-02T20:45"));
    assertTrue(
        results.stream()
            .map(Leg::getDepartureDateTime)
            .collect(Collectors.toList())
            .contains("2022-12-02T17:15"));
    assertTrue(
        results.stream()
            .map(Leg::getArrivalDateTime)
            .collect(Collectors.toList())
            .contains("2022-11-30T19:45"));
    assertTrue(
        results.stream()
            .map(Leg::getDepartureDateTime)
            .collect(Collectors.toList())
            .contains("2022-11-30T16:15"));
  }
}
