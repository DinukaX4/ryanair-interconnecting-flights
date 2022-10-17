package com.dinuka.ryanair.rest.exception;

public class RyanirException extends RuntimeException {

  public RyanirException() {}

  public RyanirException(final String message) {
    super(message);
  }
}
