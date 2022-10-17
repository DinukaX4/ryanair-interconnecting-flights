package com.dinuka.ryanair.config.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.dinuka.ryanair.exception.ValidateException;
import com.dinuka.ryanair.rest.exception.RyanairServiceException;
import com.dinuka.ryanair.rest.exception.RyanirException;
import com.dinuka.ryanair.rest.model.ErrorResponse;
import com.dinuka.ryanair.rest.model.ErrorType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(RyanairServiceException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  @ResponseBody
  public ErrorResponse handleServiceException(final RyanairServiceException ex) {
    return getErrorResponse(ex.getMessage(), ErrorType.THIRD_PARTY_SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(RyanirException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse handleThirdPartyException(final RyanairServiceException ex) {
    return getErrorResponse(ex.getMessage(), ErrorType.BAD_REQUEST);
  }

  @ExceptionHandler(ValidateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse handleValidatorException(final ValidateException ex) {
    return getErrorResponse(ex.getMessage(), ErrorType.BAD_REQUEST);
  }

  private ErrorResponse getErrorResponse(final String message, final ErrorType errorType) {
    final ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setErrorCode(errorType);
    errorResponse.setErrorDescription(message);
    return errorResponse;
  }
}
