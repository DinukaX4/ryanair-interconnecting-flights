package com.dinuka.ryanair.rest.exception;

public class RyanairServiceException extends RuntimeException {

  public RyanairServiceException() {}

  public RyanairServiceException(final String message) {
    super(message);
  }
}
