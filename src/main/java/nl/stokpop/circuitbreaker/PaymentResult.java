package nl.stokpop.circuitbreaker;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResult {
    private String status;
    private String message;
    private String transactionId;
    private LocalDateTime processedAt;
    private String errorCode;
    
    // Simple constructor for basic results
    public PaymentResult(String status, String message) {
        this.status = status;
        this.message = message;
        this.processedAt = LocalDateTime.now();
    }
    
    // Success result factory method
    public static PaymentResult success(String transactionId) {
        return new PaymentResult("SUCCESS", "Payment processed successfully", 
                                transactionId, LocalDateTime.now(), null);
    }
    
    // Failure result factory method
    public static PaymentResult failure(String errorCode, String message) {
        return new PaymentResult("FAILED", message, null, 
                                LocalDateTime.now(), errorCode);
    }
    
    // Queued result factory method
    public static PaymentResult queued(String message) {
        return new PaymentResult("QUEUED", message, null, 
                                LocalDateTime.now(), null);
    }
}
