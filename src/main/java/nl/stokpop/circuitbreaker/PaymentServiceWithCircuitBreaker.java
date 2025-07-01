package nl.stokpop.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import java.util.UUID;

public class PaymentServiceWithCircuitBreaker {
    
    private final CircuitBreaker circuitBreaker;
    private boolean serviceHealthy = false; // Start unhealthy
    
    public PaymentServiceWithCircuitBreaker() {
        this.circuitBreaker = CircuitBreakerFactory.createPaymentCircuitBreaker();
    }
    
    // Add method to control service health for demo
    public void setServiceHealthy(boolean healthy) {
        this.serviceHealthy = healthy;
        System.out.println("🏥 Service health changed to: " + (healthy ? "HEALTHY" : "UNHEALTHY"));
    }
    
    public CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }
    
    public PaymentResult processPaymentSafe(PaymentRequest request) {
        try {
            return circuitBreaker.executeSupplier(() -> {
                return callPaymentGateway(request);
            });
        } catch (CallNotPermittedException e) {
            // Circuit breaker is open
            return PaymentResult.queued("Payment queued - service temporarily unavailable");
        } catch (Exception e) {
            // Other failures
            return handlePaymentFailure(request, e);
        }
    }
    
    private PaymentResult callPaymentGateway(PaymentRequest request) {
        // Simulate payment processing with realistic delays
        try {
            Thread.sleep(100 + (int)(Math.random() * 200)); // 100-300ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate different failure rates based on service health
        double failureRate = serviceHealthy ? 0.2 : 0.6; // 20% vs 60% failure rate
        
        if (Math.random() < failureRate) {
            throw new RuntimeException("Payment gateway timeout");
        }
        
        String transactionId = UUID.randomUUID().toString().substring(0, 8);
        return PaymentResult.success(transactionId);
    }
    
    private PaymentResult handlePaymentFailure(PaymentRequest request, Exception e) {
        return PaymentResult.failure("GATEWAY_ERROR", "Payment failed: " + e.getMessage());
    }
}