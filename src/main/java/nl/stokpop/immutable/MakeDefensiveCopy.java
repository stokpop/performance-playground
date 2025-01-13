package nl.stokpop.immutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MakeDefensiveCopy {

    private final List<Date> steadyDates;

    public MakeDefensiveCopy(List<Date> dates) {
        // this is not enough: steadyDates = Collections.unmodifiableList(dates);
        steadyDates = Collections.unmodifiableList(new ArrayList<>(dates));
    }

    public boolean isDatePresent(Date date) {
        return steadyDates.contains(date);
    }
}
