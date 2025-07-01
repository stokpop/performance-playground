package nl.stokpop.circuitbreaker;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String paymentId;
    private BigDecimal amount;
    private String currency;
    private String customerId;
    private String merchantId;
    private String cardToken;
    private String description;
    
    // Constructor for simple usage
    public PaymentRequest(String paymentId, BigDecimal amount, String customerId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.customerId = customerId;
        this.currency = "EUR"; // Default
    }
}
