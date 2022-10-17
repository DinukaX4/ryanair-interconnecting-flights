package com.dinuka.ryanair.rest.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dinuka.ryanair.model.Leg;
import com.dinuka.ryanair.rest.model.Legs;

@Component
public class LegsMapper {

  public List<Legs> mapToRest(final List<Leg> legs) {
    return legs.stream()
        .map(
            leg -> {
              final Legs rest = new Legs();
              rest.setArrivalAirport(leg.getArrivalAirport());
              rest.setArrivalDateTime(leg.getArrivalDateTime());
              rest.setDepartureAirport(leg.getDepartureAirport());
              rest.setDepartureDateTime(leg.getDepartureDateTime());
              return rest;
            })
        .collect(Collectors.toList());
  }
}
