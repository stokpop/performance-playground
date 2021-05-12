package nl.stokpop.collections;

import net.jcip.annotations.Immutable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * How to make this class immutable?
 *
 * Also thinks: should the map be exposed at all?
 * Or can you provide logic in methods that act on the map?
 */
@Immutable
public class CollectionDefensiveCopy {

    private final Map<String, String> map;

    public CollectionDefensiveCopy(Map<String, String> map) {
        this.map = map;
        //this.map = new HashMap<>(map);
        //this.map = Collections.unmodifiableMap(map);
        //this.map = Collections.unmodifiableMap(new HashMap<>(map));
        //this.map = Map.copyOf(map); // returns unmodifiable map... since java 10
    }

    public Map<String, String> getMap() {
        return map;
    }

    public static void main(String[] args) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("one", "one");
        map.put("two", "two");
        CollectionDefensiveCopy testObject = new CollectionDefensiveCopy(map);
        map.clear();

        Map<String, String> exposedMap = testObject.getMap();
        System.out.println("Map size (expected 2): " + exposedMap.size());

        System.out.println("About to add value to map: " + exposedMap);
        try {
            exposedMap.put("three", "three");
            System.out.println("STATE CHANGE: ADDING NEW ELEMENT ALLOWED! " + exposedMap);
        } catch (java.lang.UnsupportedOperationException e) {
            System.out.println("EXCEPTION: ADDING NEW ELEMENT NOT ALLOWED: " + exposedMap);
        }

        System.out.println("Map size (expected 2): " + testObject.getMap().size());

    }
}
