package com.cipley.sample.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cipley.sample.model.exception.BadRequestException;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@SuppressWarnings("rawtypes")
@Configuration
public class SampleCircuitBreakerConfig {

	@Value("${circuitbreaker.failure-rate-threshold.pct}")
	private float failureRate;
	
	@Value("${circuitbreaker.min-no-of-calls}")
	private int minNumberOfCalls;
	
	@Value("${circuitbreaker.sliding-window-size}")
	private int slidingWindowSize;
	
	@Value("${circuitbreaker.wait-duration-open.ms}")
	private long waitDurationOpen;
	
	@Value("${circuitbreaker.time-limiter.ms}")
	private long timeLimiter;
	
	@Bean("sampleCircuitBreakerFactory")
	public ReactiveCircuitBreakerFactory sampleCircuitBreakerFactory() {
		return new ReactiveResilience4JCircuitBreakerFactory();
	}
	
	@Bean
	public ReactiveCircuitBreaker sampleCircuitBreaker(@Qualifier("sampleCircuitBreakerFactory") ReactiveCircuitBreakerFactory factory) {
		return factory.create("sampleCircuitBreaker");
	}
	
	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> customizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
					.circuitBreakerConfig(CircuitBreakerConfig.custom()
							.failureRateThreshold(failureRate)
							.minimumNumberOfCalls(minNumberOfCalls)
							.slidingWindowSize(slidingWindowSize)
							.waitDurationInOpenState(Duration.ofMillis(waitDurationOpen))
							.ignoreExceptions(BadRequestException.class)
							.build())
					.timeLimiterConfig(TimeLimiterConfig.custom()
							.timeoutDuration(Duration.ofMillis(timeLimiter))
							.build())
					.build());
	}
	
}
