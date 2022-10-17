package com.dinuka.ryanair.config.error;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.dinuka.ryanair.rest.exception.RyanairServiceException;
import com.dinuka.ryanair.rest.exception.RyanirException;
import com.google.common.io.CharStreams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(final ClientHttpResponse httpResponse) throws IOException {

    return (httpResponse.getStatusCode().series() == CLIENT_ERROR
        || httpResponse.getStatusCode().series() == SERVER_ERROR);
  }

  @Override
  public void handleError(final ClientHttpResponse httpResponse) throws IOException {
    final String response = toString(httpResponse.getBody());
    log.error(response);

    if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
      throw new RyanairServiceException(httpResponse.getStatusText());
    }

    throw new RyanirException(getErrorMessage(response, httpResponse.getStatusCode()));
  }

  private String toString(final InputStream inputStream) throws IOException {
    return CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .replace("[\\n]", "");
  }

  private String getErrorMessage(final String response, final HttpStatus statusCode) {

    return "Status- "
        .concat(statusCode.toString())
        .concat("\n message- [ ")
        .concat(response)
        .concat(" ]");
  }
}
