package nl.stokpop.dao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrderService {

    OrderDao orderDao = new OrderDao();

    List<Order> findDeliveredOrders(int startNumber, int endNumber) {
        return IntStream.range(startNumber, endNumber)
            .mapToObj(orderNumber -> orderDao.findOrder(orderNumber))
            .filter(Order::isDelivered)
            .collect(Collectors.toUnmodifiableList());
    }

    public static void main(String[] args) {
        OrderService service = new OrderService();
        int start = 1;
        int end = 10;
        List<Order> deliveredOrders = service.findDeliveredOrders(start, end);
        System.out.println("Delivered: " + deliveredOrders.size() + " orders.");
        System.out.println(deliveredOrders);
    }
}
