package com.dinuka.ryanair.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.dinuka.ryanair.model.Availability;
import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.model.Leg;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

class AvailabilityServiceImplTest {

  @InjectMocks private AvailabilityServiceImpl mockAvailabilityService;
  @Mock private InterConnectedFlightLegServiceImpl mockInterConnectedFlightLegService;
  @Mock private DirectFlightFlightLegServiceImpl mockDirectFlightFlightLegService;
  @Mock private RouteServiceImpl mockRouteService;
  private Gson gson;
  private FlightAvailabilityRequest request;

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
  }

  @Test
  void getAvailability() throws FileNotFoundException {

    when(mockRouteService.getInterconnectedFlightRoutes(any()))
        .thenReturn(new HashSet<>(Arrays.asList("AAL", "QWE")));

    final List<Leg> interconnectedLegs =
        gson.fromJson(
            new JsonReader(
                new FileReader(
                    Objects.requireNonNull(
                            this.getClass().getResource("/interconnected-flight-legs.json"))
                        .getFile())),
            new TypeToken<List<Leg>>() {}.getType());
    when(mockInterConnectedFlightLegService.getInterconnectedFlightLegs(any(), any(), any(), any()))
        .thenReturn(interconnectedLegs);

    final List<Leg> legs =
        gson.fromJson(
            new JsonReader(
                new FileReader(
                    Objects.requireNonNull(this.getClass().getResource("/legs.json")).getFile())),
            new TypeToken<List<Leg>>() {}.getType());

    when(mockDirectFlightFlightLegService.getFlightLegs(any(), any(), any())).thenReturn(legs);

    final List<Availability> results = mockAvailabilityService.getAvailability(request);
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.stream().anyMatch(availability -> availability.getStops() == 0));
    assertTrue(results.stream().anyMatch(availability -> availability.getStops() == 1));
  }
}
