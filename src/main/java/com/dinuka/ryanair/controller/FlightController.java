package com.dinuka.ryanair.controller;

import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.dinuka.ryanair.model.FlightAvailabilityRequest;
import com.dinuka.ryanair.rest.api.ScheduleApi;
import com.dinuka.ryanair.rest.mapper.AvailabilityRequestMapper;
import com.dinuka.ryanair.rest.mapper.ResponseMapper;
import com.dinuka.ryanair.rest.model.Response;
import com.dinuka.ryanair.service.AvailabilityService;
import com.dinuka.ryanair.util.Validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FlightController implements ScheduleApi {

  private final Validator validator;
  private final AvailabilityRequestMapper availabilityRequestMapper;
  private final ResponseMapper responseMapper;
  private final AvailabilityService availabilityService;

  @Override
  public ResponseEntity<List<Response>> availability(
      final String departure,
      final String arrival,
      @DateTimeFormat(pattern = "YYYY.MM.DD.HH.MM") final String departureDateTime,
      @DateTimeFormat(pattern = "YYYY.MM.DD.HH.MM") final String arrivalDateTime) {

    final FlightAvailabilityRequest request =
        availabilityRequestMapper.mapToServer(
            departure, arrival, departureDateTime, arrivalDateTime);

    validator.validateRequest(request);

    return ResponseEntity.ok(
        responseMapper.mapToRestObject(availabilityService.getAvailability(request)));
  }
}
