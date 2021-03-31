package nl.stokpop.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

public class OrderDao {
    Random random = new Random(System.currentTimeMillis());
    public Order findOrder(int orderNumber) {
        // simulate db query
        sleep(Math.abs(random.nextInt() % 100));
        return new Order(1, "item description", new BigDecimal("10.12"), new Date(), random.nextBoolean());
    }

    private void sleep(long durationMillis) {
        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
