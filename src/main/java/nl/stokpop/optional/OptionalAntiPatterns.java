package nl.stokpop.optional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

public class OptionalAntiPatterns {

    private static Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        Optional<BigDecimal> value = Optional.ofNullable(calculateValue(0.10));
        BigDecimal finalValue;
        if (value.isPresent()) {
            finalValue = value.get();
        } else {
            finalValue = longRemoteCallForValue();
        }
        System.out.println("Final value: " + finalValue);
    }

    private static BigDecimal calculateValue(double val) {
        return random.nextInt() % 2 == 0 ? new BigDecimal(val) : null;
    }

    private static BigDecimal longRemoteCallForValue() {
        // Simulate a long remote call
        System.out.println("Calling long remote service...");
        try {
            Thread.sleep(5000); // Simulate delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new BigDecimal("0.00"); // Default value to simulate remote result
    }
}
