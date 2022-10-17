package com.dinuka.ryanair.exception;

public class ValidateException extends RuntimeException {

  public ValidateException() {}

  public ValidateException(final String message) {
    super(message);
  }
}
