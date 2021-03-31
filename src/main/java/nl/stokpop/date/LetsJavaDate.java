package nl.stokpop.date;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LetsJavaDate {

    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static String format(final long milliseconds, String myPattern, String myZone) {
        final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT).withZone(ZoneId.of("UTC"));
        final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        final DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));
        final DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern(myPattern);
        final DateTimeFormatter formatter5 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of(myZone));
        return formatter1.format(Instant.ofEpochMilli(milliseconds));
    }

}
