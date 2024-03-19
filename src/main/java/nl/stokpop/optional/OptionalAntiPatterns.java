package nl.stokpop.optional;

import java.util.Optional;

public class OptionalAntiPatterns {

    public static void main(String[] args) {
        Optional<String> value = Optional.ofNullable("thisIsNotNull");
        System.out.println("Value is present: " + value.orElse(getFromRemoteService()));
    }

    private static String getFromRemoteService() {
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "thisIsFromRemoteService";
    }
}
