package nl.stokpop.dao;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Date;

@Value
public class Order {
    int number;
    String item;
    BigDecimal price;
    // java.util.Date is mutable, use is not advisable, use immutable date like LocalDate!
    Date date;
    boolean delivered;
}
