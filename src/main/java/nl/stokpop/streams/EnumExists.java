package nl.stokpop.streams;

import java.util.Arrays;
import java.util.Set;

public class EnumExists {
    public enum OneTwoThree {
        ONE, TWO, THREE;
        private static final Set<String> names;

        static {
            names = Set.copyOf(Arrays.stream(OneTwoThree.values()).map(Enum::name).toList());
        }

        public static boolean isOneOfEnum(String value) {
            return Arrays.stream(OneTwoThree.values()).anyMatch(v -> v.name().equals(value));
        }
        public static boolean isOneOfEnumFast(String value) {
            return names.contains(value);
        }
    }
}
