package com.dinuka.ryanair.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

  @Override
  protected void beforeRequest(final HttpServletRequest request, final String message) {
    if (StringUtils.hasText(message)) {
      log.info(message.replaceAll("[\\n\\t]", ""));
    }
  }

  @Override
  protected void afterRequest(final HttpServletRequest request, final String message) {
    if (StringUtils.hasText(message)) {
      log.info(message);
    }
  }
}
