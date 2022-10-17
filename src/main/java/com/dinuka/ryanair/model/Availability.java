package com.dinuka.ryanair.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Availability {

  private final int stops;
  private final List<Leg> legs;
}
