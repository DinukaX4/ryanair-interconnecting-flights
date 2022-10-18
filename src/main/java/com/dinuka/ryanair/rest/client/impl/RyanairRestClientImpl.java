package com.dinuka.ryanair.rest.client.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.dinuka.ryanair.rest.client.RyanairRestClient;
import com.dinuka.ryanair.rest.model.AvailabilityRequest;
import com.dinuka.ryanair.rest.model.FlightAvailability;
import com.dinuka.ryanair.rest.model.Route;
import com.dinuka.ryanair.util.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RyanairRestClientImpl implements RyanairRestClient {

  private final RetryTemplate retryTemplate;
  private final RestTemplate restTemplate;

  @Override
  @Cacheable(value = "AVAILABILITIES")
  public List<Route> getAvailableRoutes() {

    final UriComponents uriComponents =
        UriComponentsBuilder.fromUriString(Constant.BASE_URL).path("/locate/3/routes").build();

    final ResponseEntity<List<Route>> response =
        retryTemplate.execute(
            arg ->
                restTemplate.exchange(
                    uriComponents.toUri(),
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {}));

    try {
      log.debug(
          "Get all available routes status {}, response {}",
          response.getStatusCode(),
          new ObjectMapper().writeValueAsString(response.getBody()));
    } catch (JsonProcessingException e) {
      log.trace(Constant.MINOR_LOG);
    }
    return Objects.requireNonNull(response.getBody()).stream()
        .filter(
            route ->
                "RYANAIR".equals(route.getOperator())
                    && !StringUtils.hasLength(route.getConnectingAirPort()))
        .collect(Collectors.toList());
  }

  @Override
  public FlightAvailability getFlightAvailability(final AvailabilityRequest request) {
    final UriComponents uriComponents =
        UriComponentsBuilder.fromUriString(Constant.BASE_URL)
            .path("/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}")
            .buildAndExpand(
                request.getDepartureAirPort(),
                request.getArrivalAirPort(),
                request.getYear(),
                request.getMonth());

    try {
      log.info(
          "Get flight availability request {}", new ObjectMapper().writeValueAsString(request));
    } catch (JsonProcessingException e) {
      log.trace(Constant.MINOR_LOG);
    }

    final ResponseEntity<FlightAvailability> response =
        retryTemplate.execute(
            arg ->
                restTemplate.exchange(
                    uriComponents.toUri(),
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    FlightAvailability.class));

    try {
      log.info(
          "Get flight availability status {}, response {}",
          response.getStatusCode(),
          new ObjectMapper().writeValueAsString(response.getBody()));
    } catch (JsonProcessingException e) {
      log.trace(Constant.MINOR_LOG);
    }
    return response.getBody();
  }
}
