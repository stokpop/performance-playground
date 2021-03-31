package nl.stokpop.date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimePrinter;

public class LetsJodaDate {

    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static String format(final long milliseconds) {
        final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        //final DateTimeFormatter formatter = DateTimeFormat.forPattern(DEFAULT_DATETIME_FORMAT).withZoneUTC();
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(DEFAULT_DATETIME_FORMAT);
        return formatter.print(milliseconds);
    }

    public void testGoodLocalWithParams(DateTimePrinter printer, DateTimeParser parser)  {
        DateTimeFormatter good = new DateTimeFormatter(printer, parser);
    }

}
