package nl.stokpop.date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateConstructorParam {
    private final DateTimeFormatter ok; // violation
    private final DateTimeFormatter notok;
    private final DateTimeFormatter notok2 = DateTimeFormat.forPattern("YY-MM-hh"); // violation (should be static)
    private DateTimeFormatter notok3;
    public DateConstructorParam(String pattern) {
        ok = DateTimeFormat.forPattern(pattern);
        notok = DateTimeFormat.forPattern("YY-MM-hh"); // violation
    }
}




