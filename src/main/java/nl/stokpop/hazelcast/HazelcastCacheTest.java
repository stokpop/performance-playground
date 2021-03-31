package nl.stokpop.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HazelcastCacheTest {

    private HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();

    public static void main(String[] args) {
        HazelcastCacheTest test = new HazelcastCacheTest();
        test.startTest();
    }

    private void startTest() {

        FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator("newid");
        List<Long> ids = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        Map<Long, String> map = hzInstance.getMap("data");

        for (int i = 0; i < 10000; i++) {
            long key = idGenerator.newId();
            ids.add(key);
            map.put(key, "message" + i);
            map.get(key);
        }

        System.out.println("Duration: " + (System.currentTimeMillis() - startTime));

        //ids.stream().map(map::get).forEach(System.out::println);

        hzInstance.shutdown();

    }

}
