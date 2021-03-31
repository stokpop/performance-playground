package nl.stokpop.immutable;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MakeDefensiveCopy {

    private final List<Date> steadyDates;

    public MakeDefensiveCopy(List<Date> dates) {
        steadyDates = Collections.unmodifiableList(dates);
    }

    public boolean isDatePresent(Date date) {
        return steadyDates.contains(date);
    }
}
