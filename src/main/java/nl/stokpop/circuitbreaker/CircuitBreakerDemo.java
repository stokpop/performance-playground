package nl.stokpop.circuitbreaker;

import java.math.BigDecimal;

public class CircuitBreakerDemo {
    
    public static void main(String[] args) {
        PaymentServiceWithCircuitBreaker service = new PaymentServiceWithCircuitBreaker();
        
        System.out.println("=== Circuit Breaker Demo ===");
        System.out.println("Configuration: COUNT_BASED, 15 calls window, 5 min calls, 50% threshold");
        System.out.println();
        
        // Phase 1: Trigger circuit breaker with more calls
        System.out.println("🔥 Phase 1: Triggering Circuit Breaker");
        for (int i = 1; i <= 12; i++) {
            PaymentRequest request = new PaymentRequest(
                "PAY-" + i, 
                new BigDecimal("99.99"), 
                "CUSTOMER-" + i
            );
            
            PaymentResult result = service.processPaymentSafe(request);
            System.out.printf("Request %2d: %s - %s%n", 
                i, result.getStatus(), result.getMessage());
            
            // Show metrics at key points
            if (i == 5 || i == 8 || i == 12) {
                showCircuitBreakerMetrics(service);
            }
            
            try { Thread.sleep(300); } catch (InterruptedException e) { break; }
        }
        
        // Phase 2: Wait for recovery
        System.out.println("\n⏰ Phase 2: Waiting for HALF_OPEN state (6 seconds)...");
        try { Thread.sleep(6000); } catch (InterruptedException e) {}
        
        // Phase 3: Recovery with enough calls to show meaningful metrics
        System.out.println("\n🔄 Phase 3: Testing Recovery (making 12 more calls)");
        service.setServiceHealthy(true);
        
        for (int i = 21; i <= 32; i++) {
            PaymentRequest request = new PaymentRequest(
                "PAY-" + i, 
                new BigDecimal("50.00"), 
                "CUSTOMER-" + i
            );
            
            PaymentResult result = service.processPaymentSafe(request);
            System.out.printf("Recovery %d: %s - %s%n", 
                i, result.getStatus(), result.getMessage());
            
            // Show metrics progression
            if (i == 23 || i == 26 || i == 29 || i == 32) {
                showCircuitBreakerMetrics(service);
            }
            
            try { Thread.sleep(500); } catch (InterruptedException e) { break; }
        }
        
        System.out.println("\n=== Final Demo Complete ===");
        showCircuitBreakerMetrics(service);
    }
    
    private static void showCircuitBreakerMetrics(PaymentServiceWithCircuitBreaker service) {
        var cb = service.getCircuitBreaker();
        var metrics = cb.getMetrics();
        
        System.out.println("--- Circuit Breaker Metrics ---");
        System.out.printf("State: %s%n", cb.getState());
        
        // Better handling of failure rate display
        float failureRate = metrics.getFailureRate();
        if (failureRate < 0) {
            System.out.printf("Failure Rate: N/A (need %d+ calls, have %d)%n", 
                5, // minimumNumberOfCalls
                metrics.getNumberOfFailedCalls() + metrics.getNumberOfSuccessfulCalls());
        } else {
            System.out.printf("Failure Rate: %.1f%%%n", failureRate);
        }
        
        System.out.printf("Calls: %d (Failed: %d, Successful: %d)%n", 
            metrics.getNumberOfFailedCalls() + metrics.getNumberOfSuccessfulCalls(),
            metrics.getNumberOfFailedCalls(),
            metrics.getNumberOfSuccessfulCalls());
        System.out.printf("Slow Calls: %d%n", metrics.getNumberOfSlowCalls());
        System.out.println();
    }
}