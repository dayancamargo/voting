package com.assessment.voting;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@Slf4j
public class CircuitBreakerConfiguration {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(100) // 100% failure rate (1 error)
                .minimumNumberOfCalls(1)  // Minimum 1 call to evaluate
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(1)     // Sliding window size of 1
                .waitDurationInOpenState(Duration.ofSeconds(10)) // Wait 10 seconds before retrying
                .build();

        return CircuitBreakerRegistry.of(config);
    }
}