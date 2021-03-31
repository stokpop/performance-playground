package nl.stokpop.collections;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CollectionSizes {

    private final Map<String, String> fullMap;
    private final Map<String, String> fullEmptyMap;
    private final Map<String, String> fullEmptyMapSized;
    private final Map<String, String> singularMap;
    private final Map<String, String> singularEmptyMap;
    private final Map<Integer, CollectionSizes> singularEmptyMap2;

    private CollectionSizes() {
        fullMap = new HashMap<>();
        fullMap.put("one-long-key", "two-short-value");

        fullEmptyMap = new HashMap<>();
        fullEmptyMapSized = new HashMap<>(10);

        singularMap = Collections.singletonMap("one-long-key", "two-short-value");

        singularEmptyMap = Collections.emptyMap();
        singularEmptyMap2 = Collections.emptyMap();

    }
    public static void main(String[] args) throws IOException {
        CollectionSizes testObject = new CollectionSizes();
        System.out.println("Press enter to stop.");
        System.in.read();
    }
}
