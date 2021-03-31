package nl.stokpop.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestTypeTest {

    @Test
    public void testToString() {
        String toString = TestType.STRESS_TEST.toString();
        assertEquals("STRESS_TEST", toString);
    }

}