package com.dinuka.ryanair.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.dinuka.ryanair.exception.ValidateException;
import com.dinuka.ryanair.model.FlightAvailabilityRequest;

// TODO add as an annotation
@Component
public class Validator {

  public void validateRequest(final FlightAvailabilityRequest request) throws ValidateException {
    final Map<String, String> errorsMap = new HashMap<>();

    airportCodeValidator(request, errorsMap);
    datesValidator(request, errorsMap);
    if (!CollectionUtils.isEmpty(errorsMap)) {
      throw new ValidateException(errorsMap.toString());
    }
  }

  private void airportCodeValidator(
      final FlightAvailabilityRequest request, final Map<String, String> errorsMap) {
    if (!StringUtils.hasLength(request.getDepartureAirPort())) {
      errorsMap.put("INVALID_DEPARTURE", "Departure is required");
    }

    if (!StringUtils.hasLength(request.getArrivalAirPort())) {
      errorsMap.put("INVALID_ARRIVAL", "Arrival is required");
    }
  }

  private void datesValidator(
      final FlightAvailabilityRequest request, final Map<String, String> errorsMap) {
    try {
      LocalDateTime.parse(request.getArrivalTime());
      LocalDateTime.parse(request.getDepartureTime());
    } catch (DateTimeParseException e) {
      errorsMap.put("TIME_INVALID", e.getParsedString());
      return;
    }
    if (Duration.between(
                LocalDateTime.parse(request.getDepartureTime()),
                LocalDateTime.parse(request.getArrivalTime()))
            .getSeconds()
        <= 0) {
      errorsMap.put("TIME_INVALID", "Departure Time is After Arrival time");
    }
  }
}
