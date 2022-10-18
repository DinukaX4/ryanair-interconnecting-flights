package com.dinuka.ryanair.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.io.FileReader;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.rest.client.RyanairRestClient;
import com.dinuka.ryanair.rest.model.Route;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

class RouteServiceImplTest {

  @InjectMocks RouteServiceImpl mockRouteService;
  @Mock RyanairRestClient mockRestClient;

  @BeforeEach
  void setUp() throws Exception {

    openMocks(this).close();

    final Gson gson = new Gson();
    final List<Route> routes =
        gson.fromJson(
            new JsonReader(
                new FileReader(
                    Objects.requireNonNull(this.getClass().getResource("/routes.json")).getFile())),
            new TypeToken<List<Route>>() {}.getType());
    when(mockRestClient.getAvailableRoutes()).thenReturn(routes);
  }

  @Test
  void getInterconnectedFlightRoutes() {

    final Set<String> results =
        mockRouteService.getInterconnectedFlightRoutes(
            FlightAvailabilityRequest.builder()
                .departureAirPort("DUB")
                .arrivalAirPort("WRO")
                .build());

    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("AAL"));
    assertTrue(results.contains("QWE"));
  }

  @Test
  void getInterconnectedFlightRoutes_no_results() {

    final Set<String> results =
        mockRouteService.getInterconnectedFlightRoutes(
            FlightAvailabilityRequest.builder()
                .departureAirPort("ZPQ")
                .arrivalAirPort("ERT")
                .build());

    assertNotNull(results);
    assertEquals(0, results.size());
  }
}
