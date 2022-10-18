package com.dinuka.ryanair.config;

import static java.util.stream.Collectors.toMap;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import com.dinuka.ryanair.config.error.RestTemplateResponseErrorHandler;
import com.dinuka.ryanair.rest.exception.RyanairServiceException;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

@Slf4j
@EnableCaching
@EnableRetry
@EnableAsync
@Configuration
public class ApplicationConfiguration {

  private static final int HTTP_MAX_IDLE = 100;
  private static final int HTTP_KEEP_ALIVE = 30;
  private static final int HTTP_CONNECTION_TIMEOUT = 10;
  private static final int READ_TIMEOUT = 20;
  private static final int WRITE_TIMEOUT = 60;
  private static final int RETRY_INIT_INTERVAL = 5000;
  private static final int RETRY_MAX_ATTEMPTS = 3;

  private static final Map<Class<? extends Throwable>, Boolean> RETRY_EXCEPTIONS =
      Stream.of(new SimpleEntry<>(RyanairServiceException.class, true))
          .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));

  @Bean
  public RestTemplate restTemplate() {
    final RestTemplate restTemplate = new RestTemplate();

    final OkHttpClient.Builder builder = new OkHttpClient.Builder();
    final ConnectionPool okHttpConnectionPool =
        new ConnectionPool(HTTP_MAX_IDLE, HTTP_KEEP_ALIVE, TimeUnit.SECONDS);

    builder
        .connectionPool(okHttpConnectionPool)
        .retryOnConnectionFailure(true)
        .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);

    restTemplate.setRequestFactory(new OkHttp3ClientHttpRequestFactory(builder.build()));
    restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

    return restTemplate;
  }

  @Bean
  public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
    final PoolingHttpClientConnectionManager result = new PoolingHttpClientConnectionManager();
    result.setMaxTotal(20);

    return result;
  }

  @Bean
  public RequestConfig requestConfig() {
    return RequestConfig.custom()
        .setConnectionRequestTimeout(29000)
        .setConnectTimeout(29000)
        .setSocketTimeout(29000)
        .build();
  }

  @Bean
  public CloseableHttpClient httpClient(
      final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
      final RequestConfig requestConfig) {
    return HttpClientBuilder.create()
        .setConnectionManager(poolingHttpClientConnectionManager)
        .setDefaultRequestConfig(requestConfig)
        .build();
  }

  @Bean
  public RequestLoggingFilter requestLoggingFilter() {
    final RequestLoggingFilter requestLoggingFilter = new RequestLoggingFilter();
    requestLoggingFilter.setIncludeClientInfo(true);
    requestLoggingFilter.setIncludeHeaders(true);
    requestLoggingFilter.setIncludeQueryString(true);
    requestLoggingFilter.setIncludePayload(true);
    requestLoggingFilter.setMaxPayloadLength(10000);
    return requestLoggingFilter;
  }

  @Bean
  public RetryTemplate retryTemplate() {
    final RetryTemplate retryTemplate = new RetryTemplate();

    final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(RETRY_INIT_INTERVAL);
    retryTemplate.setBackOffPolicy(backOffPolicy);

    final SimpleRetryPolicy retryPolicy =
        new SimpleRetryPolicy(RETRY_MAX_ATTEMPTS, RETRY_EXCEPTIONS, true, false);
    retryTemplate.setRetryPolicy(retryPolicy);
    return retryTemplate;
  }

  @Bean
  public CacheManager cacheManager() {
    final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeineConfig());
    return caffeineCacheManager;
  }

  private Caffeine<Object, Object> caffeineConfig() {
    return Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.DAYS)
        .removalListener((key, value, cause) -> log.debug(" Token eviction due to {}", cause))
        .initialCapacity(1)
        .maximumSize(1)
        .softValues();
  }
}
