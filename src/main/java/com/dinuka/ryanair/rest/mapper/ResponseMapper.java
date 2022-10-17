package com.dinuka.ryanair.rest.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dinuka.ryanair.model.Availability;
import com.dinuka.ryanair.rest.model.Response;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResponseMapper {

  private final LegsMapper legsMapper;

  public List<Response> mapToRestObject(final List<Availability> availabilities) {
    return availabilities.stream()
        .map(
            availability -> {
              final Response response = new Response();
              response.setStops(BigDecimal.valueOf(availability.getStops()));
              response.setLegs(legsMapper.mapToRest(availability.getLegs()));
              return response;
            })
        .collect(Collectors.toList());
  }
}
