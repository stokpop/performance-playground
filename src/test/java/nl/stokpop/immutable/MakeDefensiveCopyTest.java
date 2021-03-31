package nl.stokpop.immutable;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class MakeDefensiveCopyTest {

    @Test
    void isDatePresent() {
        List<Date> dates = new ArrayList<>();
        dates.add(new Date(2020, 9, 23));
        dates.add(new Date(2020, 9, 24));
        dates.add(new Date(2020, 9, 25));

        MakeDefensiveCopy defensiveCopy = new MakeDefensiveCopy(dates);
        Date date = new Date(2020, 9, 26);
        assertFalse(defensiveCopy.isDatePresent(date));
        dates.add(date);
        assertFalse(defensiveCopy.isDatePresent(date));
    }
}