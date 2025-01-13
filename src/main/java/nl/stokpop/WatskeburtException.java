package nl.stokpop;

public class WatskeburtException extends RuntimeException {

    public WatskeburtException(String message, Throwable e) {
        super(message, e);
    }

    public WatskeburtException(String message) {
        super(message);
    }
}
