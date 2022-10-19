package com.dinuka.ryanair.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.util.DateTimeHelper.RyanairDate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

class InterConnectedFlightLegServiceImplTest {

  @InjectMocks InterConnectedFlightLegServiceImpl mockInterConnectedFlightLegService;
  @Mock DirectFlightFlightLegServiceImpl mockDirectFlightFlightLegService;

  private FlightAvailabilityRequest request;
  private RyanairDate departureDate;
  private RyanairDate arrivalDate;
  private Gson gson;

  @BeforeEach
  void setUp() throws Exception {
    openMocks(this).close();
    gson = new Gson();
    request =
        FlightAvailabilityRequest.builder()
            .arrivalTime("2022-11-02T23:45:00")
            .departureTime("2022-11-01T13:20:00")
            .departureAirPort("DUB")
            .arrivalAirPort("WRO")
            .build();

    departureDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-01T13:20:00"))
            .day(1)
            .month(11)
            .year(2022)
            .build();

    arrivalDate =
        RyanairDate.builder()
            .localDateTime(LocalDateTime.parse("2022-11-02T23:45:00"))
            .day(1)
            .month(11)
            .year(2022)
            .build();
  }

  @Test
  void getInterconnectedFlightLegs() throws FileNotFoundException {

    final List<Leg> legs =
        gson.fromJson(
            new JsonReader(
                new FileReader(
                    Objects.requireNonNull(this.getClass().getResource("/legs.json")).getFile())),
            new TypeToken<List<Leg>>() {}.getType());

    when(mockDirectFlightFlightLegService.getFlightLegs(
            arrivalDate, request.toBuilder().departureAirPort("QWE").build(), departureDate))
        .thenReturn(Collections.singletonList(legs.get(1)));

    when(mockDirectFlightFlightLegService.getFlightLegs(
            arrivalDate, request.toBuilder().arrivalAirPort("QWE").build(), departureDate))
        .thenReturn(Collections.singletonList(legs.get(0)));

    when(mockDirectFlightFlightLegService.getFlightLegs(
            arrivalDate, request.toBuilder().departureAirPort("BBL").build(), departureDate))
        .thenReturn(Collections.singletonList(legs.get(3)));

    when(mockDirectFlightFlightLegService.getFlightLegs(
            arrivalDate, request.toBuilder().arrivalAirPort("BBL").build(), departureDate))
        .thenReturn(Collections.singletonList(legs.get(2)));
    final List<Leg> results =
        mockInterConnectedFlightLegService.getInterconnectedFlightLegs(
            arrivalDate, request, departureDate, new HashSet<>(Arrays.asList("QWE", "BBL")));

    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.stream().anyMatch(leg -> "BBL".equals(leg.getArrivalAirport())));
    assertTrue(results.stream().anyMatch(leg -> "BBL".equals(leg.getDepartureAirport())));
  }

  @Test
  void getInterconnectedFlightLegs_no_results() {

    when(mockDirectFlightFlightLegService.getFlightLegs(
            arrivalDate, request.toBuilder().departureAirPort("QWE").build(), departureDate))
        .thenReturn(Collections.emptyList());

    final List<Leg> results =
        mockInterConnectedFlightLegService.getInterconnectedFlightLegs(
            arrivalDate, request, departureDate, new HashSet<>(Arrays.asList("QWE", "BBL")));

    assertNotNull(results);
    assertEquals(0, results.size());
  }
}
