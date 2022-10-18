package com.dinuka.ryanair.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public final class DateTimeHelper {

  private DateTimeHelper() {}

  public static RyanairDate getDate(final String date) {
    final LocalDateTime localDateTime = LocalDateTime.parse(date);
    return RyanairDate.builder()
        .day(localDateTime.getDayOfMonth())
        .month(localDateTime.getMonth().getValue())
        .year(localDateTime.getYear())
        .localDateTime(localDateTime)
        .build();
  }

  public static boolean isBefore(
      final LocalDateTime flightArrivalTime, final LocalDateTime arrivalTime) {
    return Duration.between(flightArrivalTime, arrivalTime).getSeconds() >= 0;
  }

  public static boolean isAfter(
      final LocalDateTime flightDepartureTime, final LocalDateTime departureTime) {
    return Duration.between(flightDepartureTime, departureTime).getSeconds() <= 0;
  }

  public static String buildDate(final String time, final LocalDateTime dateTime) {
    return String.valueOf(dateTime.toLocalDate().atTime(LocalTime.parse(time)));
  }

  public static LocalDateTime buildDate(
      final String time, final int year, final int month, final int day) {
    final LocalTime localTime = LocalTime.parse(time);
    return LocalDateTime.of(year, month, day, localTime.getHour(), localTime.getMinute());
  }

  @Builder
  @Getter
  @EqualsAndHashCode
  public static final class RyanairDate {
    private final int year;
    private final int month;
    private final int day;
    private final LocalDateTime localDateTime;
  }
}
