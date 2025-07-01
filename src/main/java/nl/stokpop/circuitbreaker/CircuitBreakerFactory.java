package nl.stokpop.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import java.time.Duration;

public class CircuitBreakerFactory {
    
    public static CircuitBreaker createPaymentCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            // Use COUNT_BASED for more predictable demo behavior
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(15) // Track last 15 calls (instead of time window)
            .minimumNumberOfCalls(5) // ⚡ Reduced from 10 to 5 for faster demo
            .failureRateThreshold(50.0f) // 50% failure rate
            .slowCallRateThreshold(100.0f) // 100% slow calls trigger opening
            .slowCallDurationThreshold(Duration.ofSeconds(2)) // 2 seconds = slow
            .waitDurationInOpenState(Duration.ofSeconds(5)) // 5 seconds wait
            .permittedNumberOfCallsInHalfOpenState(3) // Test with 3 calls
            .build();
            
        return CircuitBreaker.of("paymentService", config);
    }
}